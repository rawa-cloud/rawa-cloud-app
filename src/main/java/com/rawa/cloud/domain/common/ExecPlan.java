package com.rawa.cloud.domain.common;

import com.rawa.cloud.constant.PlanStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
public class ExecPlan extends BaseEntity {
    @ApiModelProperty(value = "计划开始时间")
    private Date startTime;

    @ApiModelProperty(value = "计划完成时间")
    private Date endTime;

    @ApiModelProperty(value = "状态")
    @Enumerated(EnumType.STRING)
    private PlanStatus execStatus;

    @ApiModelProperty(value = "执行次数")
    private Integer execCount;

    @ApiModelProperty(value = "cron表达式")
    private String cron;

    @ApiModelProperty(value = "上次运行是否成功")
    private Boolean success;

    @ApiModelProperty(value = "上次运行日志")
    @Column(length = 255 * 16)
    private String remark;

    @ApiModelProperty(value = "是否删除")
    private Boolean status;
}
