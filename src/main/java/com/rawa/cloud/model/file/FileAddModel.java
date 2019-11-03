package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class FileAddModel extends BaseModel<File> {

    @ApiModelProperty(value = "父级文件夹ID")
    private Long parentId;

    @ApiModelProperty(value = "是否是文件夹", required = true)
    @NotNull
    private Boolean dir;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "物理文件标识符")
    private String uuid;

//    @ApiModelProperty(value = "文件类型: 文件属性")
//    private String contentType;
//
//    @ApiModelProperty(value = "文件大小: 文件属性")
//    private Long size;

    @ApiModelProperty(value = "空间限制: 文件夹属性")
    private Long limitSize;

    @ApiModelProperty(value = "文件类型限制: 文件夹属性")
    private String limitSuffix;

    @ApiModelProperty(value = "是否属于个人文件, 不提供将继承父级")
    private Boolean personal;

}
