package com.rawa.cloud.runner.data;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.repository.DeptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeptDataGenerator implements DataGenerator{

    @Autowired
    DeptRepository deptRepository;

    @Override
    public void generate() {
        Dept dept = deptRepository.findByParentIsNull();
        if (dept != null) return;
        dept = new Dept();
        dept.setName("组织部门");
        dept.setParent(null);
        deptRepository.save(dept);
    }
}
