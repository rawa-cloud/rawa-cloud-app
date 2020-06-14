package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.*;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.service.PreviewService;
import com.rawa.cloud.service.PropertyService;
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


    @Override
    public File preview(File file) {
        if (isOffice(file)) {
            return convertOffice(file);
        } else if (isImage(file)) {
            return handleImage(file);
        }
        return file;
    }

    private boolean isOffice(File file) {
        String suffix = FileHelper.getSuffix(file.getName());
        return OFFICE_FILES.contains(suffix);
    }

    private boolean isImage(File file) {
        String suffix = FileHelper.getSuffix(file.getName());
        return IMAGE_FILES.contains(suffix);
    }

    private File handleImage (File file) {
        if(!"Y".equals(propertyService.getValue("preview.mark.enabled"))) return file;
        File target = new File(appProperties.getTemp(), UUID.randomUUID().toString());
        String date = DateHelper.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss");
        String content = propertyService.getValue("preview.mark.content");
        String user = "";
        if ("Y".equals(propertyService.getValue("preview.mark.user.enabled"))) {
            user = ContextHelper.getCurrentUsername();
        }
        String text = content + " " + date + " " + user;
        return WaterMarkHelper.addWatermark(file, target, text);
    }

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
