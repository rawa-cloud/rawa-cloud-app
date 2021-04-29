package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.Area;
import com.rawa.cloud.repository.AreaRepository;
import com.rawa.cloud.service.AreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {
    @Resource
    AreaRepository areaRepository;

    @Override
    public List<Area> query() {
        return areaRepository.findAll();
    }
}
