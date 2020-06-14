package com.rawa.cloud.job;

import com.rawa.cloud.bean.DynamicScheduler;
import com.rawa.cloud.job.task.JobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.ScheduledFuture;

@Slf4j
public abstract class JobDetail implements ApplicationRunner {

    @Autowired
    DynamicScheduler dynamicScheduler;


    private ScheduledFuture<?> future;

    protected abstract String getName ();

    protected abstract JobTask getJobTask ();

    protected abstract CronTrigger getCronTrigger();

    public void register () {
        ScheduledFuture<?> old = future;
        if (future != null) {
            dynamicScheduler.cancelTask(future);
            future = null;
        }
        Runnable task = getJobTask();
        CronTrigger trigger = getCronTrigger();
        if (task == null || trigger == null) {
            if (old != null) log.info("取消 Job: " + getName());
            return;
        }
        future = dynamicScheduler.addTask(task, trigger);
        log.info("注册 Job: " + getName());
    }

    public void cancel () {
        if (future != null) {
            dynamicScheduler.cancelTask(future);
            future = null;
            log.info("取消 Job: " + getName());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        register();
    }
}
