package com.rawa.cloud.job;

import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.service.DsSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyncDataJob {
    @Autowired
    AppProperties appProperties;

    @Resource
    DsSyncService dsSyncService;

    @Scheduled(cron = "${app.sync-cron}")
    public void run() {
        log.info("开始Job: 同步数据");
        try {
            dsSyncService.syncData();
            log.info("结束Job: 同步数据");
        } catch (Exception e) {
            log.error("同步数据异常", e);
            throw e;
        }
    }
}
