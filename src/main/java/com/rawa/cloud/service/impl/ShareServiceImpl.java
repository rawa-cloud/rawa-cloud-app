package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.LinkRepository;
import com.rawa.cloud.service.NasService;
import com.rawa.cloud.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    NasService nasService;

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    FileRepository fileRepository;

    @Override
    public Link getLink(Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, linkId));
        if (link.getPassword() == null || link.getPassword().isEmpty()) return link;
        // 设置伪密码
        link.setPassword("000000");
        return link;
    }

    @Override
    public List<File> queryFiles(Long linkId, String password, Long parentFileId) {
        Link link = getLinkWidthPassword(linkId, password);
        if (parentFileId == null) return link.getFileList();
        // TODO 需要更多控制
        File parent = fileRepository.findById(parentFileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentFileId));
        return fileRepository.findAllByParent(parent);
    }

    @Override
    public File getFile(Long linkId, Long fileId, String password) {
        Link link = getLinkWidthPassword(linkId, password);
        return fileRepository.findById(fileId).orElse(null);
    }

    @Override
    public java.io.File previewFile(Long linkId, Long fileId, String password) {
        return null;
    }

    @Override
    public java.io.File downloadFile(Long linkId, Long fileId, String password) {
        Link link = getLinkWidthPassword(linkId, password);
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        return nasService.download(file.getUuid(), true);
    }

    private Link getLinkWidthPassword (Long linkId, String password) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, linkId));
        if (link.getPassword() == null || link.getPassword().isEmpty()) return link;
        if (link.getPassword().equals(password)) return link;
        throw new AppException(HttpJsonStatus.SHARE_PASSWORD_EEROR, password);
    }
}
