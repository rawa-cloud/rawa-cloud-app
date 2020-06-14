package com.rawa.cloud.job.task;

import com.rawa.cloud.service.ExportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExportPlanJobTask implements JobTask{
    @Autowired
    ExportPlanService exportPlanService;

    @Override
    public void run() {
        log.info("开始执行 Job [导出计划] .....");
        exportPlanService.execPlan();
        log.info("完成执行 Job [导出计划] .....");
    }
}
