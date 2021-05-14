package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Unit extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String deptThirdId;
}
