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
public class FileInfoUpdateModel extends BaseModel<File> {

    @ApiModelProperty(value = "责任人", required = true)
    @NotEmpty
    private String leader;

    @ApiModelProperty(value = "所属辖区", required = true)
    @NotEmpty
    private String location;

    @ApiModelProperty(value = "所属单位", required = true)
    @NotEmpty
    private String unit;

    @ApiModelProperty(value = "重点单位")
    private String keyUnit;
}
