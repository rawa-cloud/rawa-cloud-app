package com.rawa.cloud.service;

import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.model.watermark.WatermarkAddModel;
import com.rawa.cloud.model.watermark.WatermarkUpdateModel;

import java.util.List;

public interface WatermarkService {

    Long add(WatermarkAddModel model);

    void update(Long id, WatermarkUpdateModel model);

    List<Watermark> query();

    Watermark get(Long id);

    void delete(Long id);
}