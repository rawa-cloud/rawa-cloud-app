package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@ApiModel
@Data
@Entity
public class FileLog extends BaseEntity {
    @ApiModelProperty(value = "文件ID")
    Long fileId;

    @ApiModelProperty(value = "操作人")
    String optBy;

    @ApiModelProperty(value = "操作时间")
    Date optTime;

    @ApiModelProperty(value = "IP地址")
    String ip;

    @ApiModelProperty(value = "类型")
    @Enumerated(EnumType.STRING)
    FileOptType type;

    @ApiModelProperty(value = "描述")
    @Column(length = 255 * 16)
    private String remark;

    // domain
    @ApiModelProperty(value = "类型描述")
    public String getTypeDesc () {
        return this.getType() != null ? this.getType().desc : null;
    }

}

