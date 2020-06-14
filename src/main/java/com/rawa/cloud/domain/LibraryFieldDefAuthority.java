package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryAuthorityOpt;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;


@ApiModel
@Data
@Entity
public class LibraryFieldDefAuthority extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LibraryFieldDef fieldDef;

    private String username;

    @Enumerated(EnumType.STRING)
    private LibraryAuthorityOpt opt;

}

