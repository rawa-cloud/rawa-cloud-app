package com.rawa.cloud.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicScheduler {
    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    public DynamicScheduler() {
        threadPoolTaskScheduler.initialize();
    }

    public ScheduledFuture<?> addTask (Runnable runnable, CronTrigger cronTrigger) {
        return threadPoolTaskScheduler.schedule(runnable, cronTrigger);
    }

    public boolean cancelTask (ScheduledFuture<?> future) {
        return future.cancel(true);
    }
}
