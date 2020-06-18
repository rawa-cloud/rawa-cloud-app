package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.model.watermark.WatermarkAddModel;
import com.rawa.cloud.model.watermark.WatermarkUpdateModel;
import com.rawa.cloud.repository.UserWatermarkRepository;
import com.rawa.cloud.repository.WatermarkRepository;
import com.rawa.cloud.service.WatermarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WatermarkServiceImpl implements WatermarkService {

    @Autowired
    WatermarkRepository watermarkRepository;

    @Autowired
    UserWatermarkRepository userWatermarkRepository;

    @Override
    @HasSuperRole
    public Long add(WatermarkAddModel model) {
        String name = model.getName();
        if (watermarkRepository.existsByName(name)) {
            throw new AppException(HttpJsonStatus.WATERMARK_EXISTS, name);
        }
        Watermark watermark = new Watermark();
        watermark.setName(name);
        watermark.setContent(model.getContent());
        watermark.setUuid(model.getUuid());
        watermark.setStatus(model.getStatus());
        return watermarkRepository.save(watermark).getId();
    }

    @Override
    @HasSuperRole
    public void update(Long id, WatermarkUpdateModel model) {
        Watermark watermark = watermarkRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.WATERMARK_NOT_FOUND, id));
        watermark.setUuid(model.getUuid());
        watermark.setContent(model.getContent());
        watermark.setStatus(model.getStatus());
        watermarkRepository.save(watermark);
    }

    @Override
    public List<Watermark> query() {
        return watermarkRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    @Override
    public Watermark get(Long id) {
        return watermarkRepository.findById(id).orElse(null);
    }

    @Override
    @HasSuperRole
    @Transactional
    public void delete(Long id) {
        Watermark watermark = watermarkRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.WATERMARK_NOT_FOUND, id));
        userWatermarkRepository.deleteAllByWatermark(watermark);
        watermarkRepository.delete(watermark);
    }
}
