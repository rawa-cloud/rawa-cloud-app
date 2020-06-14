package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Record;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.RecordRepository;
import com.rawa.cloud.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    RecordRepository recordRepository;


    @Override
    public Long add(File file, String remark) {
        Record record = new Record();
        record.setUuid(file.getUuid());
        record.setSize(file.getSize());
        record.setName(file.getName());
        record.setLastChangeTime(file.getLastChangeTime());
        record.setLastChangeBy(file.getLastChangeBy());
        record.setRemark(remark);
        record.setFile(file);
        return recordRepository.save(record).getId();
    }
}
