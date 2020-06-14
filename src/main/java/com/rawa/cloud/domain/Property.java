package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;


@ApiModel
@Data
@Entity
public class Property extends BaseEntity {

    private String name;

    @Column(length = 1024)
    private String value;

    private Boolean config;
}

