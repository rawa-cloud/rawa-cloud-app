package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Role extends BaseEntity {
    public static final String ROLE_SUPER = "SUPER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER= "USER";

    public Role() {
    }

    public Role(String roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    @Column(nullable = false, unique = true, length = 64)
    private String roleCode;

    @Column(nullable = false)
    private String roleName;

}
