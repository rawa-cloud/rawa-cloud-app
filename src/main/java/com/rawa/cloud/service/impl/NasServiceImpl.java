package com.rawa.cloud.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.Nas;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.properties.NasProperties;
import com.rawa.cloud.repository.NasRepository;
import com.rawa.cloud.service.NasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class NasServiceImpl implements NasService {

    @Autowired
    private NasProperties nasProperties;

    @Autowired
    private NasRepository nasRepository;

    @Override
    public String upload(MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        File dir = new File(nasProperties.getPath(), uuid);
        File file = new File(dir, multipartFile.getOriginalFilename());
        try {
            FileUtil.createMissingParentDirectories(file);
            multipartFile.transferTo(file);
            Nas nas = new Nas();
            nas.setStatus(false);
            nas.setCreationBy(ContextHelper.getCurrentUsername());
            nas.setUuid(uuid);
            nas.setSize(file.length());
            nas.setContentType(FileHelper.getSuffix(file.getName()));
            nasRepository.save(nas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    @Override
    public File download(String uuid, boolean privilege) {
        getNas(uuid, privilege);
        File dir = new File(nasProperties.getPath(), uuid);
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
        return files[0];
    }

    @Override
    public void delete(String uuid, boolean privilege) {
        Nas nas = getNas(uuid, privilege);
        nasRepository.delete(nas);
        File dir = new File(nasProperties.getPath(), uuid);
        FileHelper.delete(dir);
    }

    private Nas getNas (String uuid, boolean privilege) {
        Nas nas = nasRepository.findNasByUuid(uuid);
        if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
        boolean access = privilege || (!nas.getStatus() && nas.getCreationBy().equals(ContextHelper.getCurrentUsername()));
        if (!access) {
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, uuid);
        }
        return nas;
    }
}
