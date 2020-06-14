package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Date;


@ApiModel
@Data
@Entity
public class Record extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private File file;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "物理文件标识符")
    private String uuid;

    @ApiModelProperty(value = "最近修改人")
    private String lastChangeBy;

    @ApiModelProperty(value = "最近修改时间")
    private Date lastChangeTime;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "备注")
    private String remark;
}
