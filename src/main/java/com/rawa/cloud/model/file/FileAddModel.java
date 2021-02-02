package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class FileAddModel extends BaseModel<File> {

    @ApiModelProperty(value = "父级文件夹ID")
    @NotNull
    private Long parentId;

    @ApiModelProperty(value = "是否是文件夹", required = true)
    @NotNull
    private Boolean dir;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotEmpty
    @Size(max = 80)
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

    @ApiModelProperty(value = "责任人")
    private String leader;

    @ApiModelProperty(value = "所属辖区")
    private String location;

    @ApiModelProperty(value = "所属单位")
    private String unit;

    @ApiModelProperty(value = "重点单位")
    private String keyUnit;
}
