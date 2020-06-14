package com.rawa.cloud.service;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.model.dept.DeptUpdateModel;

import java.util.List;

public interface DeptService {

    Long add(DeptAddModel model);

    void update(Long id, DeptUpdateModel model);

    List<Dept> query(DeptQueryModel model);

    Dept get(Long id);

    void delete(Long id);

}