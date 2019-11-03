package com.rawa.cloud.service;

import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.model.file.FileAddModel;
import com.rawa.cloud.model.file.FilePatchModel;
import com.rawa.cloud.model.file.FileQueryModel;

import java.util.List;

public interface FileService {

    Long add (FileAddModel model);

    void update (Long id, FilePatchModel model);

    List<File> query (FileQueryModel model);

    File get (Long id);

    void delete (Long id);

    void recover (Long id);

//    Long addRootFile (String username);
//
//    void deleteRootFile (String username);

//    String getUuidForDownload (Long id, Long recordId);

      java.io.File download (Long id);

      java.io.File preview (Long id);

      void hasAuthority(Long principleId, Long fileId, boolean isUser, Umask... umasks) throws AppException;
}