package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.Dict;
import com.rawa.cloud.repository.DictRepository;
import com.rawa.cloud.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl implements DictService {

    @Autowired
    DictRepository dictRepository;

    @Override
    public void initData(List<Dict> dictList) {
        dictRepository.deleteAll();
        dictRepository.saveAll(dictList);
    }

    @Override
    public List<Dict> loadAll() {
        return dictRepository.findAll();
    }
}
