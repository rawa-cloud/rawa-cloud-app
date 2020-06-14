package com.rawa.cloud.service;

import java.util.List;

public interface PerformanceService {

    Integer getValidFileCount ();

    List<Long> getDiscVolume ();

}