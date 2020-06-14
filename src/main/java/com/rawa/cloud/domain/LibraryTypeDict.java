package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import com.rawa.cloud.domain.common.CascadeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


@ApiModel
@Data
@Entity
public class LibraryTypeDict extends CascadeEntity<LibraryTypeDict> {

    @ApiModelProperty(value = "库字典值")
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LibraryFieldDef fieldDef;

    // domain
//    public Long getParentId () {
//        return this.fieldDef != null ? this.fieldDef.getId() : null;
//    }

    @JsonIgnore
    @Transient
    public String getFullName () {
        return fieldDef.getCatalog().getName() + '/' + fieldDef.getName() + '/' + getName();
    }

}

