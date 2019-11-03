package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface DeptRepository extends BaseRepository<Dept> {
    List<Dept> findAllByParentIsNull();

}
