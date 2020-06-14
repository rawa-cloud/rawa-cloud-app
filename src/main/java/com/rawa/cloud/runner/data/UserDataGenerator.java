package com.rawa.cloud.runner.data;

import com.rawa.cloud.domain.Role;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.helper.EncryptHelper;
import com.rawa.cloud.repository.RoleRepository;
import com.rawa.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserDataGenerator implements DataGenerator{

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void generate() {
        String username = "root";
        User exist = userRepository.findUserByUsername(username);
        if (exist != null) return;
        User user = new User();
        user.setUsername(username);
        user.setStatus(true);
        user.setPassword(EncryptHelper.encrypt("123456"));
        user.setCname("超级管理员");
        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findRoleByRoleCode(Role.ROLE_SUPER);
        if (role == null) throw new RuntimeException("need Role Super first");
        roles.add(role);
        user.setRoleList(roles);
        userRepository.save(user);
    }
}
