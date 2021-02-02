package com.rawa.cloud.service;

import com.rawa.cloud.domain.Dict;

import java.util.List;

public interface DictService {
    void initData (List<Dict> dictList);

    List<Dict> loadAll ();
}