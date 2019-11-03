package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.link.LinkAddModel;
import com.rawa.cloud.model.link.LinkQueryModel;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.LinkRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    LinkRepository linkRepository;

    @Override
    public Long add(LinkAddModel model) {
        Link link = new Link();
        link.setExpiryTime(model.getExpiryTime());
        link.setPassword(model.getPassword());
        link.setCreationBy(ContextHelper.getCurrentUsername());
        link.setCreationTime(new Date());
        List<File> files = fileRepository.findAllByIdIn(model.getFiles());
        link.setFileList(files);
        link = linkRepository.save(link);
        return link.getId();
    }

//    @Override
//    public void update(Long id, LinkPatchModel model) {
//
//    }

    @Override
    public List<Link> query(LinkQueryModel model) {
        return linkRepository.findAllByCreationBy(ContextHelper.getCurrentUsername());
    }

    @Override
    public Link get(Long id) {
        return linkRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        linkRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<Long> ids) {
        List<Link> links = linkRepository.findAllById(ids);
        linkRepository.deleteInBatch(links);
    }
}
