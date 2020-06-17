package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


@ApiModel
@Data
@Entity
public class UserWatermark extends BaseEntity {

    @ApiModelProperty(value = "用户名")
    private String username;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Watermark watermark;

    @ApiModelProperty(value = "下载时使用")
    private Boolean download;

    @ApiModelProperty(value = "预览时使用")
    private Boolean preview;
}

