package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class FilePatchModel extends BaseModel<File> {
//    @ApiModelProperty(value = "父级文件夹ID")
//    private Long parentId;

//    @ApiModelProperty(value = "是否是文件夹")
//    private Boolean dir;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "物理文件标识符")
    private String uuid;

//    @ApiModelProperty(value = "文件类型: 文件属性")
//    private String contentType;

//    @ApiModelProperty(value = "文件大小: 文件属性")
//    private Long size;

    @ApiModelProperty(value = "空间限制: 文件夹属性")
    private Long limitSize;

    @ApiModelProperty(value = "文件类型限制: 文件夹属性")
    private String limitSuffix;

    @ApiModelProperty(value = "更新备注")
    private String remark;

//    @ApiModelProperty(value = "是否属于个人文件")
//    @NotNull
//    private Boolean personal;

//    @ApiModelProperty(value = "是否有效")
//    private Boolean status;
}
