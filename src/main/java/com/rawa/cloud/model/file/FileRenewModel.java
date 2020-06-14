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
public class FileRenewModel extends BaseModel<File> {

    @ApiModelProperty(value = "物理文件标识符")
    @NotEmpty
    private String uuid;

    @ApiModelProperty(value = "备注")
    private String remark;
}
