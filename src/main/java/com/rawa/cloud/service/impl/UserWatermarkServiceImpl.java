package com.rawa.cloud.service.impl;

import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.model.userwatermark.UserWatermarkAddModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkQueryModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkUpdateModel;
import com.rawa.cloud.repository.UserWatermarkRepository;
import com.rawa.cloud.repository.WatermarkRepository;
import com.rawa.cloud.service.UserWatermarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

@Service
public class UserWatermarkServiceImpl implements UserWatermarkService {

    @Autowired
    WatermarkRepository watermarkRepository;

    @Autowired
    UserWatermarkRepository userWatermarkRepository;

    @Override
    @HasSuperRole
    public Long add(UserWatermarkAddModel model) {
        Watermark watermark = watermarkRepository.findById(model.getWatermarkId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.WATERMARK_NOT_FOUND, model.getWatermarkId()));
        String username = model.getUsername();
        if (userWatermarkRepository.existsByUsernameAndWatermark(username, watermark)) {
            throw new AppException(HttpJsonStatus.USER_WATERMARK_EXISTS, null);
        }
        UserWatermark userWatermark = new UserWatermark();
        userWatermark.setUsername(username);
        userWatermark.setWatermark(watermark);
        userWatermark.setDownload(model.getDownload());
        userWatermark.setPreview(model.getPreview());
        return userWatermarkRepository.save(userWatermark).getId();
    }

    @Override
    @HasSuperRole
    public void update(Long id, UserWatermarkUpdateModel model) {
        UserWatermark userWatermark = userWatermarkRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_WATERMARK_NOT_FOUND, id));
        userWatermark.setPreview(model.getPreview());
        userWatermark.setDownload(model.getDownload());
        userWatermarkRepository.save(userWatermark);
    }

    @Override
    public Page<UserWatermark> query(UserWatermarkQueryModel model) {
        String username = model.getUsername();
        Watermark watermark = model.getWatermarkId() == null ? null
                : watermarkRepository.findById(model.getWatermarkId()).orElse(null);
        Boolean download = model.getDownload();
        Boolean preview = model.getPreview();
        return userWatermarkRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!StringUtils.isEmpty(username)) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("username"), username));
            }
            if (watermark != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("watermark"), watermark));
            }
            if (download != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("download"), download));
            }
            if (preview != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("preview"), preview));
            }
            return predicate;
        }), model.toPage(false, "creationTime"));
    }

    @Override
    public UserWatermark get(Long id) {
        return userWatermarkRepository.findById(id).orElse(null);
    }

    @Override
    @HasSuperRole
    public void delete(Long id) {
        userWatermarkRepository.deleteById(id);
    }
}
