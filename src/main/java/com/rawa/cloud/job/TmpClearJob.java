package com.rawa.cloud.job;

import com.rawa.cloud.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TmpClearJob {

    @Autowired
    AppProperties appProperties;

    @Scheduled(cron = "0 0 4 ? * MON")
    public void clear() {
        log.info("开始Job: 清理temp临时文件夹");
        // 清理超过24小时临时文件
        File root = new File(appProperties.getTemp());
        List<File> clearFiles = Arrays.stream(root.listFiles()).filter(s -> {
            long old = s.lastModified();
            long now = new Date().getTime();
            return now - old > 1000 * 60 * 60 * 24 * 7;
        }).collect(Collectors.toList());
        for (File tmp: clearFiles) {
            FileSystemUtils.deleteRecursively(tmp);
            log.info("删除临时文件: " + tmp.getName());
        }
        log.info("结束Job: 清理temp临时文件夹");
    }
}
