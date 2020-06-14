package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface UserRepository extends BaseRepository<User> {
    User findUserByUsername (String username);

    List<User> findAllByDept(Dept dept);
}
