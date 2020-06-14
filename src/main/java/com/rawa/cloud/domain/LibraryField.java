package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryFieldType;
import com.rawa.cloud.constant.LibraryVisibility;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@ApiModel
@Data
@Entity
public class LibraryField extends BaseEntity {


    @ApiModelProperty(value = "字段值")
    private String value;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LibraryFieldDef fieldDef;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Library library;

    // domain
    public String getName () {
        return this.fieldDef == null ? "" : this.fieldDef.getName();
    }

    public Long getFieldDefId () {
        return this.fieldDef == null ? null : this.fieldDef.getId();
    }

    public Long getLibraryId () {
        return this.library == null ? null : this.library.getId();
    }

    public LibraryFieldType getType () {
        return this.fieldDef == null ? null : this.fieldDef.getType();
    }

    @ApiModelProperty(value = "字典列表")
    public List<LibraryTypeDict> getOptions () {
        return this.fieldDef == null ? null : this.fieldDef.getTypeDictList();
    }

    public Boolean getVisible () {
        return this.fieldDef.getVisible();
    }

    public Boolean getDisabled () {
        return this.fieldDef.getDisabled();
    }
}

