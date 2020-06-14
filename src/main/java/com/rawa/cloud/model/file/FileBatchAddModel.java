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
public class FileBatchAddModel extends BaseModel<File> {

    @ApiModelProperty(value = "父级文件夹ID")
    private Long parentId;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotEmpty
    @Size(max = 80)
    private String name;

    @ApiModelProperty(value = "是否是文件夹", required = true)
    @NotNull
    private Boolean dir;

    @ApiModelProperty(value = "物理文件标识符")
    private String uuid;

    @ApiModelProperty(value = "子目录")
    private List<FileBatchAddModel> children;
}
