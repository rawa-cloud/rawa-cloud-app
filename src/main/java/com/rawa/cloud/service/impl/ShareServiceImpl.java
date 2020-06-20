package com.rawa.cloud.service.impl;

import com.google.common.collect.Lists;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.LinkRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    NasService nasService;

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileService fileService;

    @Autowired
    PreviewService previewService;

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
        User user = userRepository.findUserByUsername(link.getCreationBy());
        if (user == null) throw new AppException(HttpJsonStatus.USER_NOT_FOUND, link.getCreationBy());
        if (parentFileId == null) return link.getFileList()
                .stream()
                .filter(s -> s.getStatus() && Boolean.TRUE.equals(fileService.hasAuthority(s, user, true, Umask.ACCESS)))
                .collect(Collectors.toList());
        File parent = fileRepository.findById(parentFileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentFileId));
        boolean access = Boolean.TRUE.equals(fileService.hasAuthority(parent, user, true, Umask.ACCESS));
        if (!access) return Lists.newArrayList();
        return fileRepository.findAllByParent(parent)
                .stream()
                .filter(s -> s.getStatus() && !Boolean.FALSE.equals(fileService.hasAuthority(s, user, false, Umask.ACCESS)))
                .collect(Collectors.toList());
    }

    @Override
    public File getFile(Long linkId, Long fileId, String password) {
        Link link = getLinkWidthPassword(linkId, password);
        return fileRepository.findById(fileId).orElse(null);
    }

    @Override
    public java.io.File previewFile(Long linkId, Long fileId, String password) {
        Link link = getLinkWidthPassword(linkId, password);
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        User user = userRepository.findUserByUsername(link.getCreationBy());
        if (user == null) throw new AppException(HttpJsonStatus.USER_NOT_FOUND, link.getCreationBy());
        return previewService.preview(nasService.download(file.getUuid(), true), user.getUsername());
    }

    @Override
    public java.io.File downloadFile(Long linkId, Long fileId, String password) {
        Link link = getLinkWidthPassword(linkId, password);
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        User user = userRepository.findUserByUsername(link.getCreationBy());
        if (user == null) throw new AppException(HttpJsonStatus.USER_NOT_FOUND, link.getCreationBy());
        return fileService.download(fileId, user, true);
    }

    private Link getLinkWidthPassword (Long linkId, String password) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, linkId));
        if (link.getPassword() == null || link.getPassword().isEmpty()) return link;
        if (link.getPassword().equals(password)) return link;
        throw new AppException(HttpJsonStatus.SHARE_PASSWORD_ERROR, password);
    }
}
