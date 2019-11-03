package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.CascadeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel
@Data
@Entity
public class Dept extends CascadeEntity<Dept> {

    @ApiModelProperty(value = "部门名称")
    private String name;
}
