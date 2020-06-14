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
public class FileUpdateModel extends BaseModel<File> {

    @ApiModelProperty(value = "空间限制: 文件夹属性")
    private Long limitSize;

    @ApiModelProperty(value = "文件类型限制: 文件夹属性")
    private String limitSuffix;

}
