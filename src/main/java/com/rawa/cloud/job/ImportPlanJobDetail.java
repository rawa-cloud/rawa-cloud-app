package com.rawa.cloud.job;

import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.job.task.ImportPlanJobTask;
import com.rawa.cloud.job.task.JobTask;
import com.rawa.cloud.service.ImportPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class ImportPlanJobDetail extends JobDetail{
    @Autowired
    ImportPlanService importPlanService;

    @Autowired
    ImportPlanJobTask importPlanJobTask;

    @Override
    protected String getName() {
        return "导入计划";
    }

    @Override
    protected JobTask getJobTask() {
        return this.importPlanJobTask;
    }

    @Override
    protected CronTrigger getCronTrigger() {
        ImportPlan plan = importPlanService.getPlan();
        if (plan == null) return null;
        return new CronTrigger(plan.getCron());
    }
}
