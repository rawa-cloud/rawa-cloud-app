package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.BeanHelper;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptPatchModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.repository.DeptRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.DeptService;
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

    @Override
    @HasSuperRole
    @Transactional
    public Long add(DeptAddModel model) {
        Dept dept = generateAddDept(model);
        deptRepository.save(dept);
        return dept.getId();
    }

    @Override
    @HasSuperRole
    public void update(Long id, DeptPatchModel model) {
        Dept dept = generatePatchDept(id, model);
        deptRepository.save(dept);
    }

    @Override
    @Transactional
    public List<Dept> query(DeptQueryModel model) {
        Example<Dept> example = generateQueryDept(model);
        return deptRepository.findAll(example);
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
        // 删除该部门下用户
        List<User> users = userRepository.findAllByDept(dept);
        users.stream().forEach(s -> userService.delete(s.getId()));
        deptRepository.deleteById(id);
    }

    // inner
    private Dept generateAddDept(DeptAddModel model) {
        Long parentId = model.getParentId();
        Dept dept = new Dept();
        Dept parent = getParent(parentId, null);
        dept.setParent(parent);
        if (exists(parent, model.getName())) throw new AppException(HttpJsonStatus.DEPT_EXISTS, model.getName());
        dept.setName(model.getName());
        return dept;
    }

    private Example<Dept> generateQueryDept(DeptQueryModel model) {
        Long parentId = model.getParentId();
        Dept dept = new Dept();
        dept.setName(model.getName());
        ExampleMatcher matcher = null;
        if (parentId != null) {
            if (parentId < 0) {
                matcher = ExampleMatcher.matching()
                        .withIncludeNullValues()
                        .withIgnorePaths(BeanHelper.getNullFields(dept, "parent"));
            }else{
                Dept parent = deptRepository.findById(parentId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, parentId));
                dept.setParent(parent);
            }
        }
        return matcher == null ? Example.of(dept) : Example.of(dept, matcher);
    }

    private Dept generatePatchDept (Long id, DeptPatchModel model) {
        Dept dept = deptRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, id));
        Long parentId = model.getParentId();
        Dept parent = getParent(parentId, dept.getParent());
        dept.setParent(parent);
        if (exists(parent, model.getName())) throw new AppException(HttpJsonStatus.DEPT_EXISTS, model.getName());
        dept.setName(model.getName());
        return dept;
    }

    private boolean exists(Dept parent, String deptName) {
        List<Dept> siblings = parent == null ? deptRepository.findAllByParentIsNull() : parent.getChildren();
        return siblings.stream().anyMatch(s -> s.getName().equals(deptName));
    }

    private Dept getParent (Long parentId, Dept old) {
        if (parentId == null) return old;
        Dept parent = null;
        if (parentId >= 0) {
            parent = deptRepository.findById(parentId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, parentId));
        }
        return parent;
    }
}
