package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Dict extends BaseEntity {
    private String name;

    private String code;

    private String label;

    private String parentKey;

    private String remark;

    private Boolean status;
}
