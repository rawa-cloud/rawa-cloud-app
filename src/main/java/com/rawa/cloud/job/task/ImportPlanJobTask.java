package com.rawa.cloud.job.task;

import com.rawa.cloud.service.ImportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImportPlanJobTask implements JobTask{
    @Autowired
    ImportPlanService importPlanService;

    @Override
    public void run() {
        log.info("开始执行 Job [导入计划] .....");
        importPlanService.execPlan();
        log.info("完成执行 Job [导入计划] .....");
    }
}
