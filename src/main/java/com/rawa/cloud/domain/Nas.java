package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel
@Data
@Entity
public class Nas extends BaseEntity {

    @ApiModelProperty(value = "文件唯一标识")
    private String uuid;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "大小")
    private Long size;

    @ApiModelProperty(value = "文件类型")
    private String contentType;

    @ApiModelProperty(value = "有效性")
    private Boolean status;

    @ApiModelProperty(value = "创建人")
    private String creationBy;
}
