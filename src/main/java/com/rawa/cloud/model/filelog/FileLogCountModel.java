package com.rawa.cloud.model.filelog;

import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FileLogCountModel extends BaseModel<FileLog> {
    @ApiModelProperty(value = "文件ID")
    @NotNull
    private Long fileId;

    @ApiModelProperty(value = "操作类型")
    @NotNull
    private FileOptType type;
}
