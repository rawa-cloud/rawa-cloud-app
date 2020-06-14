package com.rawa.cloud.service;

import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.model.log.LogQueryModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LogService {

    Long add(LogModule module, LogType type, String remark);

    Long add(Log log);

    Long add(Log log, String username);

    Page<Log> query (LogQueryModel model);

}