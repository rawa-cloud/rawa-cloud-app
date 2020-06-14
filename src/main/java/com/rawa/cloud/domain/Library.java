package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryAuthorityOpt;
import com.rawa.cloud.constant.LibraryVisibility;
import com.rawa.cloud.domain.common.BaseEntity;
import com.rawa.cloud.helper.ContextHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@ApiModel
@Data
@Entity
public class Library extends BaseEntity {

    @ApiModelProperty(value = "库名称")
    private String name;

//    @ApiModelProperty(value = "有效性")
//    private Boolean status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LibraryCatalog catalog;

    @JsonIgnore
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibraryAuthority> authorityList;

    @JsonIgnore
    @ApiModelProperty(value = "库文件列表")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "library_file",
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private List<File> fileList;

    @JsonIgnore
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LibraryField> fieldList;

    @ApiModelProperty(value = "可见性")
    @Enumerated(EnumType.STRING)
    private LibraryVisibility visibility;

    // domain
    @ApiModelProperty(value = "库分类ID")
    public Long getCatalogId () {
        return this.catalog != null ? this.catalog.getId() : null;
    }

    @ApiModelProperty(value = "字段列表")
    public List<LibraryField> getFields () {
        List<LibraryField> ret = new ArrayList<>();
        for (LibraryFieldDef x : this.catalog.getFieldDefs()) {
            LibraryField field = null;
            for (LibraryField y : this.fieldList) {
                if (y.getFieldDef().getId().equals(x.getId())) {
                    field = y;
                }
            }
            if (field == null) {
                field = new LibraryField();
                field.setFieldDef(x);
            }
            ret.add(field);
        }
        ret = ret.stream().filter(s -> Boolean.TRUE.equals(s.getVisible())).collect(Collectors.toList());
        return ret;
    }

    @ApiModelProperty(value = "文件")
    @Transient
    public File getFile () {
        File ret = null;
        if (this.fileList.size() > 0) {
             ret = this.fileList.get(0);
            if (ret != null) ret.setFilePath(ret.getPath());
        }
        return ret;
    }

    @ApiModelProperty(value = "权限列表")
    public List<LibraryAuthority> getAuthorities () {
        List<LibraryAuthority> ret = getAuthorityList();
        if (!CollectionUtils.isEmpty(ret)) {
            for(LibraryAuthority authority: ret) {
                if (authority.getUsername().equals(authority.getCreatedBy())) authority.setSystem(true);
            }
        }
//        if (!CollectionUtils.isEmpty(getCatalog().getAuthorityList())) {
//            for(LibraryCatalogAuthority authority: getCatalog().getAuthorityList()) {
//                boolean has = ret.stream().anyMatch(s -> s.getUsername().equals(authority.getUsername()));
//                if (!has) {
//                    LibraryAuthority item = new LibraryAuthority();
//                    item.setOpt(authority.getOpt());
//                    item.setInherit(true);
//                    item.setUsername(authority.getUsername());
//                    ret.add(item);
//                }
//            }
//        }
        return ret;
    }

    @Transient
    public String getCreatedUser () {
        return this.getCreatedBy();
    }

    @Transient
    public boolean getAdmin () {
        if(LibraryVisibility.all.equals(this.visibility)) return true;
        String username = ContextHelper.getCurrentUsername();
        return getAuthorities().stream()
                .anyMatch(s -> s.getUsername().equals(username) && LibraryAuthorityOpt.a.equals(s.getOpt()));
    }

    @Transient
    public boolean getEditable () {
        if(LibraryVisibility.all.equals(this.visibility)) return true;
        if (getAdmin()) return true;
        String username = ContextHelper.getCurrentUsername();
        return getAuthorities().stream()
                .anyMatch(s -> s.getUsername().equals(username) && LibraryAuthorityOpt.w.equals(s.getOpt()));
    }

    @JsonIgnore
    @Transient
    public String getFullName () {
        return catalog.getName() + '/' + getName();
    }
}

