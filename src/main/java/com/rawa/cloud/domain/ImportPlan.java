package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.ExecPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;


@ApiModel
@Data
@Entity
public class ImportPlan extends ExecPlan {

    @ApiModelProperty(value = "原文件地址")
    private String filePath;

    @ApiModelProperty(value = "原文件大小")
    private Long size;

    @ApiModelProperty(value = "父级文件夹")
    private Long parentId;
}

