package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.common.CascadeEntity;
import com.rawa.cloud.exception.AppException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.function.Function;


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

    @JsonIgnore
    @ApiModelProperty(value = "文件所有者")
    @ManyToOne
    private User user;

    private Boolean system;

    // 该处需要优化， 应该独立出去， 不然查询次数过多
//    @JsonIgnore
//    @ApiModelProperty(value = "文件修改历史列表")
//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(name = "file_record",
//            inverseJoinColumns = @JoinColumn(name = "record_id"))
//    private List<Record> records;


    // domain

    @ApiModelProperty(value = "是否是根节点")
    @Transient
    private boolean isLeaf;

    @ApiModelProperty(value = "文件权限码")
    @Transient
    private Long umask;

    @ApiModelProperty(value = "文件路径")
    @Transient
    private String filePath;

    @ApiModelProperty(value = "是否拥有管理权限")
    @Transient
    private boolean admin;

    @ApiModelProperty(value = "个人文件用户ID")
    @Transient
    public Long getUserId () {
        return this.user == null ? null : this.user.getId();
    }

    @Transient
    public boolean isRoot () {
        return getParent() == null && "/".equals(getName()) && getDir();
    }

    @Transient
    public boolean isUserRoot () {
        File parent = getParent();
        return "Users".equals(getName()) && getDir() && (parent != null && parent.isRoot());
    }

    @Transient
    public boolean isSystemFile () {
        return Boolean.TRUE.equals(system);
    }

    @JsonIgnore
    public String getPath () {
        File temp = this;
        StringBuilder sb = new StringBuilder("");
        while (temp != null) {
            if (Boolean.TRUE.equals(temp.getSystem())) {
                break;
            }
            sb.insert(0,  "/" + temp.getName());
            temp = temp.getParent();
        }
        return sb.toString();
    }

    @JsonIgnore
    public long getActualSize (boolean all) {
        if (!all && !Boolean.TRUE.equals(status)) return 0;
        if (!dir) return size == null ? 0 : size;
        long ret = 0L;
        List<File> ls = getChildren();
        if (CollectionUtils.isEmpty(ls)) return ret;
        for(File f: ls) {
            ret += f.getActualSize(all);
        }
        return ret;
    }

    @JsonIgnore
    public boolean exists (String name) {
        if (!dir || !status || CollectionUtils.isEmpty(getChildren())) return false;
        return getChildren().stream()
                .filter(s -> Boolean.TRUE.equals(s.getStatus()))
                .anyMatch(s -> s.getName().equals(name));
    }
}
