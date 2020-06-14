package com.rawa.cloud.model.exportplan;

import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.validator.annotation.Cron;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Data
public class ExportPlanAddModel extends BaseModel<ExportPlan> {

    @ApiModelProperty(value = "cron表达式", required = true)
    @NotEmpty
    @Cron
    private String cron;

//    @ApiModelProperty(value = "导出文件地址", required = true)
//    @NotEmpty
//    private String filePath;
}
