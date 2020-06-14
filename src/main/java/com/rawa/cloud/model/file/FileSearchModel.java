package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel
@Data
public class FileSearchModel extends BaseModel<File> {

    @ApiModelProperty(value = "文件名称", required = true)
    @Size(max = 80)
    private String name;

    @ApiModelProperty(value = "创建人")
    private String creationBy;

    @ApiModelProperty(value = "创建时间开始")
    private Date creationTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    private Date creationTimeEnd;
}
