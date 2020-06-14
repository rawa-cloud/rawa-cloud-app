package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class FileQueryModel extends BaseModel<File> {

    @ApiModelProperty(value = "父级文件夹ID")
    @NotNull
    private Long parentId;

    @ApiModelProperty(value = "是否是文件夹")
    private Boolean dir;

//    @ApiModelProperty(value = "创建人")
//    private String creationBy;

//    @ApiModelProperty(value = "创建时间")
//    private Date creationTime;
//
//    @ApiModelProperty(value = "最近修改人")
//    private String lastChangeBy;
//
//    @ApiModelProperty(value = "最近修改时间")
//    private Date lastChangeDate;

//    @ApiModelProperty(value = "是否有效")
//    private Boolean status;

//    @ApiModelProperty(value = "文件名称", required = true)
//    @NotEmpty
//    private String name;

//    @ApiModelProperty(value = "文件类型: 文件属性")
//    private String contentType;
//
//    @ApiModelProperty(value = "文件大小: 文件属性")
//    private Long size;
//
//    @ApiModelProperty(value = "空间限制: 文件夹属性")
//    private Long limitSize;
//
//    @ApiModelProperty(value = "文件类型限制: 文件夹属性")
//    private String limitSuffix;

//    @ApiModelProperty(value = "是否属于个人文件")
//    private Boolean personal;

}
