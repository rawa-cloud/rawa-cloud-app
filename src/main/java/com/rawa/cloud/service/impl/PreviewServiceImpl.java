package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.*;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.UserWatermarkRepository;
import com.rawa.cloud.service.NasService;
import com.rawa.cloud.service.PreviewService;
import com.rawa.cloud.service.PropertyService;
import com.rawa.cloud.service.UserWatermarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PreviewServiceImpl implements PreviewService {

    static final List<String> OFFICE_FILES = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "csv");
    static final List<String> IMAGE_FILES = Arrays.asList("gif", "ico", "jpg", "jpeg", "png");

    @Autowired
    AppProperties appProperties;

    @Autowired
    PropertyService propertyService;

    @Autowired
    UserWatermarkService userWatermarkService;

    @Autowired
    NasService nasService;

    @Override
    public File preview(File file, String username) {
        return userWatermarkService.generateWatermark(file, username, "preview");
//        if (isOffice(file)) {
//            return convertOffice(file);
//        } else if (isImage(file)) {
//            return handleImage(file);
//        }
//        return file;
    }

    private boolean isOffice(File file) {
        String suffix = FileHelper.getSuffix(file.getName());
        return OFFICE_FILES.contains(suffix);
    }

//    private boolean isImage(File file) {
//        String suffix = FileHelper.getSuffix(file.getName());
//        return IMAGE_FILES.contains(suffix);
//    }
//
//    private File handleImage (File file) {
//        UserWatermark userWatermark = userWatermarkRepository.findByUsername(ContextHelper.getCurrentUsername());
//        if (userWatermark == null || !Boolean.TRUE.equals(userWatermark.getPreview()) return file;
//        Watermark watermark = userWatermark.getWatermark();
//        if (watermark == null || !Boolean.TRUE.equals(watermark.getStatus())) return file;
//        File target = new File(appProperties.getTemp(), UUID.randomUUID().toString());
//        File logo = watermark.getUuid() != null ? nasService.download(watermark.getUuid(), true): null;
//        return WaterMarkHelper.addWatermark(file, target, logo, watermark.getContent());
//    }

    private File convertOffice (File file) {
//        String filename = EncryptHelper.MD5(file.getAbsolutePath());
//        String storePath = appProperties.getOfficeTemp() + '/' + filename;
//        File target = new File(storePath);
//        if(target.exists()) return target;
//        File newTarget = HttpHelper.upload(appProperties.getOfficeUrl(), "files", file);
//        try {
//            FileCopyUtils.copy(newTarget, target);
//            newTarget.delete();
//        } catch (Exception e) {
//            throw new AppException(HttpJsonStatus.OFFICE_CONVERT_FAIL, e);
//        }
//        return target;
        return null;
    }
}
