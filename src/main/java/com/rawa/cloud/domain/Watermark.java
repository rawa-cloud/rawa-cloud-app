package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;


@ApiModel
@Data
@Entity
public class Watermark extends BaseEntity {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "logo地址")
    private String uuid;

    @ApiModelProperty(value = "水印内容")
    private String content;

    @ApiModelProperty(value = "状态：开启/关闭")
    private Boolean status;
}

