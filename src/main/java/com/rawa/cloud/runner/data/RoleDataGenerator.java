package com.rawa.cloud.runner.data;

import com.rawa.cloud.domain.Role;
import com.rawa.cloud.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleDataGenerator implements DataGenerator{

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void generate() {
        Map<String, String> roleNameMap = new HashMap<>();
        roleNameMap.put(Role.ROLE_SUPER, "超级管理员");
        roleNameMap.put(Role.ROLE_ADMIN, "文件夹管理员");
        roleNameMap.put(Role.ROLE_USER, "普通用户");
        Set<String> roleCodes = roleNameMap.keySet();
        List<Role> roles = new ArrayList<>();
        for(String roleCode : roleCodes) {
            Role exist = roleRepository.findRoleByRoleCode(roleCode);
            if (exist != null) continue;
            Role role = new Role();
            role.setRoleCode(roleCode);
            role.setRoleName(roleNameMap.get(roleCode));
            roles.add(role);
        }
        if (roles.size() > 0) {
            roleRepository.saveAll(roles);
        }
    }
}
