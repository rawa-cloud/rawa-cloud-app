package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.CascadeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


// 需要指定一个别名， 不然swagger会使用 java.io.File, 原因尚不清楚
@ApiModel(value = "ApiFile")
@Data
@Entity
public class File extends CascadeEntity<File> {


    @ApiModelProperty(value = "是否是文件夹")
    private Boolean dir;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件类型: 文件属性")
    private String contentType;

    @ApiModelProperty(value = "文件大小: 文件属性")
    private Long size;

    @ApiModelProperty(value = "物理文件标识符")
    private String uuid;

    @ApiModelProperty(value = "创建人")
    private String creationBy;

    @ApiModelProperty(value = "创建时间")
    private Date creationTime;

    @ApiModelProperty(value = "最近修改人")
    private String lastChangeBy;

    @ApiModelProperty(value = "最近修改时间")
    private Date lastChangeTime;

    @ApiModelProperty(value = "是否有效")
    private Boolean status;

    @ApiModelProperty(value = "空间限制: 文件夹属性")
    private Long limitSize;

    @ApiModelProperty(value = "文件类型限制: 文件夹属性")
    private String limitSuffix;

    @ApiModelProperty(value = "是否属于个人文件")
    private Boolean personal;

//    @JsonIgnore
    @ApiModelProperty(value = "文件修改历史列表")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "file_record",
            inverseJoinColumns = @JoinColumn(name = "record_id"))
    private List<Record> records;


    // domain

    // 是否属于特定文件夹
    @JsonIgnore
    public boolean isChild (Long id) {
        File temp = this;
        while (temp != null) {
            if (id.equals(temp.getId())) return true;
            temp = temp.getParent();
        }
        return false;
    }

    @JsonIgnore
    public String getPath () {
        File temp = this;
        StringBuilder sb = new StringBuilder();
        while (temp != null) {
            sb.insert(0,  temp.getName());
            temp = temp.getParent();
            if(temp != null) sb.insert(0, "/");
        }
        return sb.toString();
    }

}
