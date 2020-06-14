package com.rawa.cloud.service.impl;

import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.service.PerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PerformanceServiceImpl implements PerformanceService {
    @Autowired
    FileRepository fileRepository;

    @Override
    public Integer getValidFileCount() {
        return fileRepository.countAllByStatusIsTrue();
    }

    @Override
    public List<Long> getDiscVolume() {
        File[] disks = File.listRoots();
        List<Long> ret = new ArrayList();
        if (disks.length > 0) {
            File root = disks[0];
            ret.add(root.getUsableSpace());
            ret.add(root.getTotalSpace());
        }
        return ret;
    }
}
