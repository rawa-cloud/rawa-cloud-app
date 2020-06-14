package com.rawa.cloud.model.log;

import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class LogQueryModel extends PageModel<Log> {
    @ApiModelProperty(value = "模块")
    private LogModule module;

    @ApiModelProperty(value = "类型")
    private LogType type;

    @ApiModelProperty(value = "操作人")
    private String operateBy;

}
