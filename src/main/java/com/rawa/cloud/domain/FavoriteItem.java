package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel
@Data
@Entity
public class FavoriteItem extends BaseEntity {

    // JPA

    @ApiModelProperty(value = "收藏名称")
    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private FavoriteCatalog catalog;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private File file;

    @ApiModelProperty(value = "备注")
    private String remark;

    // domain

    @Transient
    public Long getFileId () {
        return this.file.getId();
    }

    @Transient
    public Long getFileParentId () {
        File parent = this.file.getParent();
        return parent == null ? null : parent.getId();
    }
}
