package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.Area;
import com.rawa.cloud.domain.Unit;
import com.rawa.cloud.repository.AreaRepository;
import com.rawa.cloud.repository.UnitRepository;
import com.rawa.cloud.service.AreaService;
import com.rawa.cloud.service.UnitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {
    @Resource
    UnitRepository unitRepository;

    @Override
    public List<Unit> query() {
        return unitRepository.findAll();
    }
}
