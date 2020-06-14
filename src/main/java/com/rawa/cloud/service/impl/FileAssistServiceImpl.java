package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.model.fileassist.FileAssistAutoSaveRequestModel;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.service.FileAssistService;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.NasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
public class FileAssistServiceImpl implements FileAssistService {
    @Autowired
    AppProperties appProperties;

    @Autowired
    NasService nasService;

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepository;

    @Override
    public File download(Long id) {
        com.rawa.cloud.domain.File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        return nasService.download(file.getUuid(), false);
    }

    @Override
    @Transactional
    public void autoSave(Long id, FileAssistAutoSaveRequestModel model) {
        try {
            if (!model.getStatus().equals(2)) return;
            com.rawa.cloud.domain.File file = fileRepository.findById(id)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
            String downloadUri = model.getUrl();
            log.info("自动保存文件下载地址: " + downloadUri);
            URL url = new URL(downloadUri);
            String host = url.getHost() + ":" + url.getPort();
            url = new URL(downloadUri.replace(host, appProperties.getOfficeHost()));
            log.info("自动保存文件转换后下载地址: " + url.toString());
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();

            String username = CollectionUtils.isEmpty(model.getUsers()) ? "" : model.getUsers().get(0);
            String uuid = UUID.randomUUID().toString();
            File savedFile = nasService.createEmptyFile(uuid, file.getName());
            OutputStream out = new FileOutputStream(savedFile);
            FileCopyUtils.copy(stream, out);
            nasService.upload(uuid, username, savedFile);
            fileService.autoSave(id, uuid, username);
        }catch (Exception e) {
            throw new RuntimeException("自动保存失败", e);
        }
   }
}
