package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.BeanHelper;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.model.dept.DeptUpdateModel;
import com.rawa.cloud.repository.DeptRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.DeptService;
import com.rawa.cloud.service.LogService;
import com.rawa.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {


    @Autowired
    DeptRepository deptRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @Override
    @HasSuperRole
    @Transactional
    public Long add(DeptAddModel model) {
        Long parentId = model.getParentId();
        String name = model.getName();
        Dept parent = deptRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, parentId));
        if (exists(parent, name)) throw new AppException(HttpJsonStatus.DEPT_EXISTS, name);

        Dept dept = new Dept();
        dept.setParent(parent);
        dept.setName(name);
        dept.setName(model.getName());
        Long id = deptRepository.save(dept).getId();
        logService.add(Log.build(LogModule.DEPT, LogType.ADD).lc(parent.getName()).add("名称", name, null).end());
        return id;
    }

    @Override
    @HasSuperRole
    @Transactional
    public void update(Long id, DeptUpdateModel model) {
        Dept dept = deptRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, id));
        if (dept.isDefaultDept()) {
            throw new AppException(HttpJsonStatus.OPT_NOT_ALLOWED, "不允许修改系统默认部门", id);
        }
        Long parentId = model.getParentId();
        Log log = Log.build(LogModule.DEPT, LogType.UPDATE);
        log.lc(dept.getName());
        if(!parentId.equals(dept.getParentId())) { // 更新父级
            Dept parent = deptRepository.findById(parentId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, parentId));
            log.add("父级部门", parent.getName(), dept.getParent().getName());
            dept.setParent(parent);
        }
        // 判断名称是否重复
        if (exists(dept.getParent(), model.getName())) throw new AppException(HttpJsonStatus.DEPT_EXISTS, model.getName());
        if (!dept.getName().equals(model.getName())) {
            log.add("名称", model.getName(), dept.getName());
        }
        dept.setName(model.getName());
        logService.add(log.end());
        deptRepository.save(dept);
    }

    @Override
    @Transactional
    public List<Dept> query(DeptQueryModel model) {
        String name = model.getName();
        Long parentId = model.getParentId();
        Dept parent =  parentId == null ? null : deptRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, parentId));
        Dept query = new Dept();
        query.setParent(parent);
        query.setName(name);
        return deptRepository.findAll(Example.of(query));
    }

    @Override
    public Dept get(Long id) {
        return deptRepository.findById(id).orElse(null);
    }

    @Override
    @HasSuperRole
    @Transactional
    public void delete(Long id) {
        Dept dept = deptRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, id));
        if (dept.isDefaultDept()) {
            throw new AppException(HttpJsonStatus.OPT_NOT_ALLOWED, "不允许删除系统默认部门", id);
        }
        // 删除该部门下用户
        List<User> users = userRepository.findAllByDept(dept);
        users.stream().forEach(s -> userService.delete(s.getId()));
        logService.add(Log.build(LogModule.DEPT, LogType.DELETE).lc(dept.getName()).end());
        deptRepository.delete(dept);
    }


    private boolean exists(Dept parent, String deptName) {
        List<Dept> siblings = parent.getChildren();
        return siblings.stream().anyMatch(s -> s.getName().equals(deptName));
    }
}
