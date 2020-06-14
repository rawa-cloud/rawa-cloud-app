package com.rawa.cloud.service.impl;

import com.google.common.collect.Lists;
import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.link.LinkAddModel;
import com.rawa.cloud.model.link.LinkQueryModel;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.LinkRepository;
import com.rawa.cloud.service.FileLogService;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.LinkService;
import com.rawa.cloud.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.rawa.cloud.helper.DateHelper.toDate;
import static com.rawa.cloud.helper.DateHelper.toLocalDate;

@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    FileService fileService;

    @Autowired
    LogService logService;

    @Autowired
    FileLogService fileLogService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    LinkRepository linkRepository;

    @Override
    @Transactional
    public Long add(LinkAddModel model) {
        Link link = new Link();
        link.setExpiryTime(model.getExpiryTime());
        link.setPassword(model.getPassword());
        link.setCreationBy(ContextHelper.getCurrentUsername());
        link.setCreationTime(new Date());
        List<File> files = fileRepository.findAllByIdIn(model.getFiles());
        link.setFileList(files);
        link = linkRepository.save(link);
        logService.add(Log.build(LogModule.SHARE, LogType.ADD).lc(link.getFileName()).st("分享").end());
        for (File f : files) {
            fileLogService.add(f.getId(), FileOptType.share, "");
        }
        return link.getId();
    }

//    @Override
//    public void update(Long id, LinkPatchModel model) {
//
//    }

    @Override
    public Page<Link> query(LinkQueryModel model) {
        return linkRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("creationBy"), ContextHelper.getCurrentUsername()));

            Predicate p1 = criteriaBuilder.isNull(root.get("expiryTime"));
            Predicate p2 = criteriaBuilder.greaterThan(root.get("expiryTime"), toDate(toLocalDate(new Date())));
            predicate.getExpressions().add(criteriaBuilder.or(p1, p2));

            return predicate;
        }), model.toPage(false, "creationTime"));
    }

    @Override
    public Link get(Long id) {
        return linkRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Link link = linkRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LINK_NOT_FOUND, id));
        logService.add(Log.build(LogModule.SHARE, LogType.DELETE).lc(link.getFileName()).st("取消分享").end());
        linkRepository.delete(link);
    }

    @Override
    @Transactional
    public void deleteInBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }
}
