package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.ExecPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;


@ApiModel
@Data
@Entity
public class ExportPlan extends ExecPlan {
    @ApiModelProperty(value = "文件根目录")
    private String filePath;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "导出文件路径")
    private String exportFilePath;
}

