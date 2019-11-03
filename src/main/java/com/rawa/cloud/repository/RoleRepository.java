package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Role;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface RoleRepository extends BaseRepository<Role> {
    List<Role> findAllByRoleCodeIn(List<String> roleCodes);

    Role findRoleByRoleCode(String roleCode);
}
