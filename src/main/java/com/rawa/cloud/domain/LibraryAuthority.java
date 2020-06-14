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
public class LibraryAuthority extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Library library;

    private String username;

    @Enumerated(EnumType.STRING)
    private LibraryAuthorityOpt opt;

    @Transient
    private Boolean system;

}

