package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LibraryAuthorityOpt;
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
public class LibraryCatalog extends BaseEntity {

    @ApiModelProperty(value = "库分类名称")
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "catalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LibraryFieldDef> fieldDefs;

    @OneToMany(mappedBy = "catalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibraryCatalogAuthority> authorityList;

    @ApiModelProperty(value = "可见性")
    @Enumerated(EnumType.STRING)
    private LibraryVisibility visibility;

    @Transient
    public boolean getAddable () {
        if(LibraryVisibility.all.equals(this.visibility)) return true;
        String username = ContextHelper.getCurrentUsername();
        if (CollectionUtils.isEmpty(authorityList)) return false;
        return authorityList.stream()
                .anyMatch(s -> s.getUsername().equals(username) && LibraryAuthorityOpt.a.equals(s.getOpt()));
    }
}

