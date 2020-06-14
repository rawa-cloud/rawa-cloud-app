package com.rawa.cloud.service;

import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.model.filelog.FileLogQueryModel;
import com.rawa.cloud.model.log.LogQueryModel;
import org.springframework.data.domain.Page;

public interface FileLogService {

    Long add(Long fileId, FileOptType type, String remark);

    Long add(Long fileId, FileOptType type, String username, String remark);

    Page<FileLog> query (FileLogQueryModel model);

    int count (Long fileId, FileOptType type);
}