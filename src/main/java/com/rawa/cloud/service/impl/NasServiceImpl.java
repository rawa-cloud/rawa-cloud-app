package com.rawa.cloud.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.Nas;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.properties.NasProperties;
import com.rawa.cloud.repository.NasRepository;
import com.rawa.cloud.service.NasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NasServiceImpl implements NasService {

    @Autowired
    private NasProperties nasProperties;

    @Autowired
    private NasRepository nasRepository;

    @Override
    public String upload(MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        String filename = multipartFile.getOriginalFilename();
        String[] temp = filename.split("/");
        filename = temp[temp.length - 1];
        File file = createEmptyFile(uuid, filename);
        try {
            multipartFile.transferTo(file);
            Nas nas = new Nas();
            nas.setStatus(false);
            nas.setCreationBy(ContextHelper.getCurrentUsername());
            nas.setUuid(uuid);
            nas.setSize(file.length());
            nas.setContentType(FileHelper.getSuffix(file.getName()));
            nas.setName(file.getName());
            nasRepository.save(nas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    @Override
    public void upload(String uuid, String username, File file) {
        Nas nas = new Nas();
        nas.setStatus(false);
        nas.setCreationBy(username);
        nas.setUuid(uuid);
        nas.setSize(file.length());
        nas.setContentType(FileHelper.getSuffix(file.getName()));
        nas.setName(file.getName());
        nasRepository.save(nas);
    }

    @Override
    public String upload(File file, String username) {
        String uuid = UUID.randomUUID().toString();
        String filename = file.getName();
        Nas nas = new Nas();
        nas.setStatus(true);
        nas.setCreationBy(username);
        nas.setUuid(uuid);
        nas.setSize(file.length());
        nas.setContentType(FileHelper.getSuffix(username));
        nas.setName(file.getName());

        File storeFile = createEmptyFile(uuid, filename);
        try {
            FileCopyUtils.copy(file, storeFile);
        } catch (IOException e) {
            log.error("储存物理文件失败 :" + file.getAbsolutePath(), e);
            throw new RuntimeException("储存物理文件失败 :" + file.getAbsolutePath(), e);
        }
        nasRepository.save(nas);
        return uuid;
    }

    @Override
    public File createEmptyFile(String uuid, String filename) {
        File dir = new File(nasProperties.getPath(), uuid);
        File file = new File(dir, filename);
        FileUtil.createMissingParentDirectories(file);
        return file;
    }

    @Override
    @Transactional
    public List<String> upload(MultipartFile[] files) {
        List<String> ret = new LinkedList<>();
        try {
           for (MultipartFile f : files) {
               String uuid = UUID.randomUUID().toString();
               File file = createEmptyFile(uuid, f.getOriginalFilename());
               f.transferTo(file);
               Nas nas = new Nas();
               nas.setStatus(false);
               nas.setCreationBy(ContextHelper.getCurrentUsername());
               nas.setUuid(uuid);
               nas.setSize(file.length());
               nas.setContentType(FileHelper.getSuffix(file.getName()));
               nas.setName(file.getName());
               nasRepository.save(nas);
               ret.add(uuid);
           }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public File download(String uuid, boolean privilege) {
        getNas(uuid, privilege);
        File dir = new File(nasProperties.getPath(), uuid);
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            log.error(HttpJsonStatus.RAW_FILE_LOST.getMessage() + " : " + uuid);
            throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
        }
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
//        不再划分特权
//        boolean access = privilege || (!nas.getStatus() && nas.getCreationBy().equals(ContextHelper.getCurrentUsername()));
//        if (!access) {
//            throw new AppException(HttpJsonStatus.ACCESS_DENIED, uuid);
//        }
        return nas;
    }
}
