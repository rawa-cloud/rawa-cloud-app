package com.rawa.cloud.model.importplan;

import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.validator.annotation.Cron;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class ImportPlanAddModel extends BaseModel<ImportPlan> {

    @ApiModelProperty(value = "cron表达式", required = true)
    @NotEmpty
    @Cron
    private String cron;

    @ApiModelProperty(value = "导入文件地址", required = true)
    @NotEmpty
    private String filePath;

    @ApiModelProperty(value = "导入文件父级", required = true)
    @NotNull
    private Long parentId;
}
