package com.rawa.cloud.model.filelog;

import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FileLogQueryModel extends PageModel<FileLog> {
    @ApiModelProperty(value = "文件ID")
    @NotNull
    private Long fileId;
}
