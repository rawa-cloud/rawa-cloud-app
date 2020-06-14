package com.rawa.cloud.job;

import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.job.task.ExportPlanJobTask;
import com.rawa.cloud.job.task.JobTask;
import com.rawa.cloud.service.ExportPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class ExportPlanJobDetail extends JobDetail{
    @Autowired
    ExportPlanService exportPlanService;

    @Autowired
    ExportPlanJobTask exportPlanJobTask;

    @Override
    protected String getName() {
        return "导出计划";
    }

    @Override
    protected JobTask getJobTask() {
        return this.exportPlanJobTask;
    }

    @Override
    protected CronTrigger getCronTrigger() {
        ExportPlan plan = exportPlanService.getPlan();
        if (plan == null) return null;
        return new CronTrigger(plan.getCron());
    }
}
