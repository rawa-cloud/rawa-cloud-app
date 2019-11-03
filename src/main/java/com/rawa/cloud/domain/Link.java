package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@ApiModel
@Data
@Entity
public class Link extends BaseEntity {

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "创建人")
    private String creationBy;

    @ApiModelProperty(value = "创建时间")
    private Date creationTime;

    @ApiModelProperty(value = "失效时间")
    private Date expiryTime;


    @JsonIgnore
    @ApiModelProperty(value = "外链文件列表")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "link_file",
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private List<File> fileList;

    public Boolean getMultiple () {
        return this.fileList != null && this.fileList.size() > 1;
    }

    public String getContentType () {
        if (this.fileList == null && this.fileList.get(0) == null) return "";
        return this.fileList.get(0).getContentType();
    }

    public String getFileName () {
        if (this.fileList == null && this.fileList.get(0) == null) return "";
        return this.fileList.get(0).getName();
    }

    public Boolean getDir () {
        return this.fileList != null && this.fileList.get(0) != null && this.fileList.get(0).getDir();
    }

}

