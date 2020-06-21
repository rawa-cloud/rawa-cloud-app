package com.rawa.cloud.service;

import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.model.userwatermark.UserWatermarkAddModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkQueryModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkUpdateModel;
import org.springframework.data.domain.Page;

import java.io.File;

public interface UserWatermarkService {

    Long add(UserWatermarkAddModel model);

    void update(Long id, UserWatermarkUpdateModel model);

    Page<UserWatermark> query(UserWatermarkQueryModel model);

    UserWatermark get(Long id);

    void delete(Long id);

    File generateWatermark (File source, String username, String type);
}