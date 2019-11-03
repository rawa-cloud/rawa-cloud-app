package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.service.PreviewService;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PreviewServiceImpl implements PreviewService {

    static final List<String> OFFICE_FILES = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx");

    @Autowired
    private DocumentConverter documentConverter;

    @Autowired
    AppProperties appProperties;


    @Override
    public File preview(File file) {
        if (isOffice(file)) {
            return convertOffice(file);
        }
        return file;
    }

    private boolean isOffice(File file) {
        String suffix = FileHelper.getSuffix(file.getName());
        return OFFICE_FILES.contains(suffix);
    }

    private File convertOffice (File file) {
        String basePath = appProperties.getTemp();
        File target = new File(basePath + "/" + file.getName() + ".pdf");
        // 转换文件
        if (!target.exists()) {
            try {
                documentConverter.convert(file).to(target).execute();
            } catch (Exception e) {
                log.error(HttpJsonStatus.OFFICE_CONVERT_FAIL.getMessage(), e);
                throw new AppException(HttpJsonStatus.OFFICE_CONVERT_FAIL, e);
            }
        }
        return target;
    }
}
