package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.bean.Licence;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.*;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.EncryptHelper;
import com.rawa.cloud.helper.LangHelper;
import com.rawa.cloud.model.user.*;
import com.rawa.cloud.repository.DeptRepository;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.RoleRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.LogService;
import com.rawa.cloud.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeptRepository deptRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileService fileService;

    @Autowired
    LogService logService;

    @Autowired
    Licence licence;

    @Override
    @HasSuperRole
    @Transactional
    public Long add(UserAddModel model) {
        checkUserCount();
        User exist = userRepository.findUserByUsername(model.getUsername());
        if (exist != null) throw new AppException(HttpJsonStatus.USER_EXISTS, model.getUsername());
        Dept dept = deptRepository.findById(model.getDeptId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, model.getDeptId()));
        User user = new User();
        user.setDept(dept);
        user.setUsername(model.getUsername());
        user.setCname(model.getCname());
        user.setIp(model.getIp());
        user.setStatus(true);

        String encryptPassword = EncryptHelper.encrypt(model.getPassword());
        user.setPassword(encryptPassword);

        if (model.getRoles() != null) {
            List<Role> roles = roleRepository.findAllByRoleCodeIn(model.getRoles());
            user.setRoleList(roles);
        }

        if (model.getFiles() != null) {
            List<File> files = fileRepository.findAllById(model.getFiles());
            user.setFileList(files);
        }

        Long id = userRepository.save(user).getId();

        Log log = Log.build(LogModule.USER, LogType.ADD).lc(user.getId());
        log.add("用户名", user.getUsername(), null)
            .add("中文名", user.getCname(), null)
            .add("所属部门", user.getDeptName(), null)
            .add("IP段", user.getIp(), null)
            .add("角色", user.getRoleList().toString(), null)
            .end();
        logService.add(log);
        return id;
    }

    @Override
    @HasSuperRole
    @Transactional
    public void update(Long id, UserUpdateModel model) {
        if (model.getStatus()) {
            checkUserCount();
        }
        User user = userRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, id));
        if (user.isDefaultUser()) {
            throw new AppException(HttpJsonStatus.OPT_NOT_ALLOWED, "不允许修改系统默认用户", id);
        }
        String cname = model.getCname();
        String ip = model.getIp();
        Boolean status = model.getStatus();
        Long deptId = model.getDeptId();
        List<String> roles = model.getRoles();
        List<Long> files = model.getFiles();

        Log log = Log.build(LogModule.USER, LogType.UPDATE).lc(user.getUsername());

        if(!StringUtils.equals(cname, user.getCname())) {
            log.add("中文名", cname, user.getCname());
            user.setCname(cname);
        }

        if(!StringUtils.equals(ip, user.getIp())) {
            log.add("IP段", ip, user.getIp());
            user.setIp(ip);
        }

        if(!user.getStatus().equals(status)) {
            log.add("状态", Boolean.TRUE.equals(status) ? "有效" : "无效", Boolean.TRUE.equals(user.getStatus()) ? "有效" : "无效");
            user.setStatus(status);
        }

        if(!user.getDeptId().equals(deptId) ) {
            Dept dept = deptRepository.findById(deptId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, deptId));
            log.add("归属部门", dept.getName(), user.getDeptName());
            user.setDept(dept);
        }

        if(roles != null && !LangHelper.equals(roles, user.getRoles())) {
            List<Role> newRoles = roleRepository.findAllByRoleCodeIn(model.getRoles());
            log.add("角色", newRoles.toString(), user.getRoleList().toString());
            user.setRoleList(newRoles);
        }
        // 如果没有管理员权限， 将删除管理文件夹
        if (!roles.contains(Role.ROLE_ADMIN)) {
            user.getFileList().clear();
        }
        logService.add(log.end());
        userRepository.save(user);
    }

    @Override
    public List<User> query(UserQueryModel model) {
        User query = new User();
        query.setUsername(model.getUsername());
        query.setCname(model.getCname());
        query.setStatus(model.getStatus());
        Long detId = model.getDeptId();
        Dept dept = detId == null ? null : deptRepository.findById(detId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, detId));
        if (dept != null) {
            dept.setParent(null);
            query.setDept(dept);
        }
        return userRepository.findAll(Example.of(query));
    }

    @Override
    public Page<User> queryForPage(UserQueryPageModel model) {
        User query = new User();
        if(!StringUtils.isEmpty(model.getUsername())) query.setUsername(model.getUsername());
        if(!StringUtils.isEmpty(model.getCname())) query.setCname(model.getCname());
        if(model.getStatus() != null) query.setStatus(model.getStatus());
        Long detId = model.getDeptId();
        Dept dept = detId == null ? null : deptRepository.findById(detId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, detId));
        if (dept != null) {
            dept.setParent(null);
            query.setDept(dept);
        }
        return userRepository.findAll(Example.of(query), model.toPage(false, "createdDate"));
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @HasSuperRole
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, id));
        if (user.isDefaultUser()) {
            throw new AppException(HttpJsonStatus.OPT_NOT_ALLOWED, "不允许删除系统默认用户", id);
        }
        List<File> userFiles = fileRepository.findAllByUser(user);
        fileRepository.deleteAll(userFiles); // 删除个人文件
        logService.add(Log.build(LogModule.USER, LogType.DELETE).lc(user.getUsername()).end());
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = userRepository.findUserByUsername(ContextHelper.getCurrentUsername());
        if (user == null) throw new AppException(HttpJsonStatus.USER_NOT_FOUND, ContextHelper.getCurrentUsername());
        boolean pass = EncryptHelper.check(oldPassword, ContextHelper.getCurrentUser().getPassword());
        if (!pass) throw new AppException(HttpJsonStatus.AUTH_PASSWORD_FAIL, null);
        String encodePassword = EncryptHelper.encrypt(newPassword);
        user.setPassword(encodePassword);
        userRepository.save(user);
        logService.add(Log.build(LogModule.AUTH, LogType.UPDATE).st("更改密码").lc(user.getUsername()).end());
        ContextHelper.getCurrentUser().setPassword(encodePassword);
    }

    @Override
    @HasSuperRole
    public List<Long> addUserFiles(Long id, UserFilesAddModel model) {
        User user = userRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, id));
        user.setFileList(fileRepository.findAllById(model.getFiles()));
        userRepository.save(user);
        return user.getFiles();
    }

    private void checkUserCount () {
        User query = new User();
        query.setStatus(true);
        int count =  (int)userRepository.count(Example.of(query));
        if (!licence.checkLimitUser(count)) throw new AppException(HttpJsonStatus.LICENSE_USER_LIMIT, licence.getLimitUser());
    }

}
