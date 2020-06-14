package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.filelog.FileLogQueryModel;
import com.rawa.cloud.repository.FileLogRepository;
import com.rawa.cloud.service.FileLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class FileLogServiceImpl implements FileLogService {

    @Autowired
    FileLogRepository fileLogRepository;

    @Override
    public Long add(Long fileId, FileOptType type, String remark) {
        return add(fileId, type, null, remark);
    }

    @Override
    public Long add(Long fileId, FileOptType type, String username, String remark) {
        String ip = ContextHelper.getIp();
        FileLog log = new FileLog();
        log.setFileId(fileId);
        log.setIp(ip);
        log.setOptBy(StringUtils.isEmpty(username) ? ContextHelper.getCurrentUsername() : username);
        log.setOptTime(new Date());
        log.setType(type);
        log.setRemark(remark);
        return fileLogRepository.save(log).getId();
    }

    @Override
    public Page<FileLog> query (FileLogQueryModel model) {
        return fileLogRepository.findAllByFileIdOrderByOptTimeDesc(model.getFileId(), model.toPage());
    }

    @Override
    public int count(Long fileId, FileOptType type) {
        return fileLogRepository.countAllByFileIdAndType(fileId, type);
    }
}
