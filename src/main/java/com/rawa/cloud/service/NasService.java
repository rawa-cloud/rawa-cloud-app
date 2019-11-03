package com.rawa.cloud.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface NasService {

    String upload (MultipartFile multipartFile);

    File download (String uuid, boolean privilege);

    void delete (String uuid, boolean privilege);

}
