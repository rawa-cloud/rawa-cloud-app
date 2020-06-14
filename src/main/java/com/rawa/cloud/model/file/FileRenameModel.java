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
public class FileRenameModel extends BaseModel<File> {

    @ApiModelProperty(value = "文件名")
    @NotEmpty
    @Size(max = 80)
    private String name;
}
