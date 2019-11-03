package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Role;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.BeanHelper;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.EncryptHelper;
import com.rawa.cloud.model.user.UserAddModel;
import com.rawa.cloud.model.user.UserPatchModel;
import com.rawa.cloud.model.user.UserQueryModel;
import com.rawa.cloud.repository.DeptRepository;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.RoleRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    @Override
    @HasSuperRole
    @Transactional
    public Long add(UserAddModel model) {
        User user = generateAddUser(model);
        user = userRepository.save(user);
//        fileService.addRootFile(model.getUsername());
        return user.getId();
    }

    @Override
    @HasSuperRole
    @Transactional
    public void update(Long id, UserPatchModel model) {
        User user = generatePatchUser(id, model);
        userRepository.save(user);
    }

    @Override
    public List<User> query(UserQueryModel model) {
        Example<User> example = generateQueryUser(model);
        return userRepository.findAll(example);
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @HasSuperRole
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
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
        ContextHelper.getCurrentUser().setPassword(encodePassword);
    }

    // inner
    private User generateAddUser (UserAddModel model) {
        User exist = userRepository.findUserByUsername(model.getUsername());
        if (exist != null) throw new AppException(HttpJsonStatus.USER_EXISTS, model.getUsername());
        Dept dept = null;
        if (model.getDeptId() != null) {
            dept = deptRepository.findById(model.getDeptId())
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, model.getDeptId()));
        }
        User user = new User();
        user.setDept(dept);
        user.setUsername(model.getUsername());
        user.setCname(model.getCname());
        user.setIp(model.getIp());
        user.setStatus(true);

        if (model.getRoles() != null) {
            List<Role> roles = roleRepository.findAllByRoleCodeIn(model.getRoles());
            user.setRoleList(roles);
        }

        if (model.getFiles() != null) {
            List<File> files = fileRepository.findAllById(model.getFiles());
            user.setFileList(files);
        }

        String encryptPassword = EncryptHelper.encrypt(model.getPassword());
        user.setPassword(encryptPassword);

        return user;
    }

    private Example<User> generateQueryUser (UserQueryModel model) {
        User user = new User();
        user.setId(model.getId());
        user.setUsername(model.getUsername());
        user.setCname(model.getCname());
        user.setStatus(model.getStatus());
        ExampleMatcher matcher = null;
        if (model.getDeptId() != null) {
            if (model.getDeptId() < 0) {
                matcher = ExampleMatcher.matching()
                        .withIncludeNullValues()
                        .withIgnorePaths(BeanHelper.getNullFields(user, "dept"));
            }else{
                Dept dept = deptRepository.findById(model.getDeptId())
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, model.getDeptId()));
                user.setDept(dept);
            }
        }
        return matcher != null ? Example.of(user, matcher) : Example.of(user);
    }

    private User generatePatchUser (Long id, UserPatchModel model) {
        User user = userRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, id));
        String cname = model.getCname();
        String ip = model.getIp();
        Boolean status = model.getStatus();
        Long deptId = model.getDeptId();
        List<String> roles = model.getRoles();
        List<Long> rootFiles = model.getFiles();
        if(cname != null) user.setCname(cname);
        if(ip != null) user.setIp(ip);
        if(status != null) user.setStatus(status);
        if(deptId != null) {
            Dept dept = null;
            if (deptId >= 0) {
                dept = deptRepository.findById(deptId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, deptId));
            }
            user.setDept(dept);
        }
        if(roles != null) {
            user.setRoleList(roleRepository.findAllByRoleCodeIn(model.getRoles()));
        }
        if (rootFiles != null) {
            List<File> files = fileRepository.findAllById(model.getFiles());
            user.setFileList(files);
        }
        return user;
    }

}
