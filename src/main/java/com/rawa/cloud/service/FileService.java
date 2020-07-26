package com.rawa.cloud.service;

import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Record;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.model.file.*;

import java.util.List;

public interface FileService {

    Long add(FileAddModel model);

    void update(Long id, FileUpdateModel model);

    List<File> query(FileQueryModel model);

    File get(Long id);

    void delete(List<Long> ids);

    File getRootFile();

    File getUserRootFile();

    java.io.File download(Long id, boolean watermark, Integer height, Integer width);

    java.io.File download(Long id, User user, boolean watermark, Integer height, Integer width);

    java.io.File preview(Long id);

    void rename(Long id, FileRenameModel model);

    void renew (Long id, FileRenewModel model);

    void autoSave (Long id, String uuid, String username);

    List<Record> getRecords (Long id);

    List<File> getParents(Long id);

    /**
     * 在非隐式中，如果不存在权限定义则返回null，其他情况则为true/false
     * @param file
     * @param implicit
     * @param umasks
     * @return
     * @throws AppException
     */
    Boolean hasAuthority(File file, boolean implicit, Umask... umasks) throws AppException;

    Boolean hasAuthority(File file, User user, boolean implicit, Umask... umasks) throws AppException;

    List<Long> batchAdd(List<FileBatchAddModel> model);

    int move(FileMoveModel model);

    int copy(FileMoveModel model);

    List<File> search(FileSearchModel model);

    File createUserFile (String username);

    List<File> getAdminRoots();

    void deleteUserFile(String username);

    java.io.File exportFile (java.io.File base);

    void importFile (java.io.File importFile, File parent);
}
