package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryAuthorityOpt;
import com.rawa.cloud.constant.LibraryFieldType;
import com.rawa.cloud.constant.LibraryVisibility;
import com.rawa.cloud.domain.common.BaseEntity;
import com.rawa.cloud.helper.ContextHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.List;


@ApiModel
@Data
@Entity
public class LibraryFieldDef extends BaseEntity {

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段类型")
    @Enumerated(EnumType.STRING)
    private LibraryFieldType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LibraryCatalog catalog;

    @OneToMany(mappedBy = "fieldDef", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibraryFieldDefAuthority> authorityList;

    @ApiModelProperty(value = "可见性")
    @Enumerated(EnumType.STRING)
    private LibraryVisibility visibility;

    @OneToMany(mappedBy = "fieldDef", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LibraryTypeDict> typeDictList;

    // domain
//    public Long getParentId () {
//        return this.catalog != null ? this.catalog.getId() : null;
//    }

    @JsonIgnore
    @Transient
    public String getFullName () {
        return catalog.getName() + '/' + getName();
    }

    public Boolean getVisible () {
        if (getVisibility() == null || LibraryVisibility.all.equals(getVisibility())) return true;
        if (CollectionUtils.isEmpty(getAuthorityList())) return false;
        String username = ContextHelper.getCurrentUsername();
        boolean has = getAuthorityList().stream().anyMatch(s -> {
            return s.getUsername() != null
                    && s.getUsername().equals(username)
                    && (LibraryAuthorityOpt.r.equals(s.getOpt()) || LibraryAuthorityOpt.w.equals(s.getOpt()));
        });
        return has;
    }

    public Boolean getDisabled () {
        if (getVisibility() == null || LibraryVisibility.all.equals(getVisibility())) return false;
        if (CollectionUtils.isEmpty(getAuthorityList())) return true;
        String username = ContextHelper.getCurrentUsername();
        boolean has = getAuthorityList().stream().anyMatch(s -> {
            return s.getUsername() != null
                    && s.getUsername().equals(username)
                    && LibraryAuthorityOpt.w.equals(s.getOpt());
        });
        return !has;
    }
}

