package com.rawa.cloud.service;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;

import java.util.List;

public interface ShareService {

    Link getLink (Long linkId);

    List<File> queryFiles (Long linkId, String password, Long parentFileId);

    File getFile (Long linkId, Long fileId, String password);

    java.io.File previewFile (Long linkId, Long fileId, String password);

    java.io.File downloadFile (Long linkId, Long fileId, String password);

}