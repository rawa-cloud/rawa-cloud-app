package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryAuthorityOpt;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;


@ApiModel
@Data
@Entity
public class LibraryOptLog extends BaseEntity {


    @ApiModelProperty(value = "操作人")
    private String operator;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Library library;

    @Column(length = 255 * 16)
    private String remark;

}

