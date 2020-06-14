package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel
@Data
public class FileMoveModel extends BaseModel<File> {

    @ApiModelProperty(value = "源文件列表")
    @NotEmpty
    private List<Long> sources;

    @ApiModelProperty(value = "目标文件")
    @NotNull
    private Long target;

}
