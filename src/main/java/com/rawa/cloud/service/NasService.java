package com.rawa.cloud.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface NasService {

    String upload (MultipartFile file);

    void upload (String uuid, String username, File file);

    String upload (java.io.File file, String username);

    File createEmptyFile (String uuid, String filename);

    List<String> upload (MultipartFile[] files);

    File download (String uuid, boolean privilege);

    void delete (String uuid, boolean privilege);

}
