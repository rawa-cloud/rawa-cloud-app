package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.recycle.RecycleAddInBatchModel;
import com.rawa.cloud.model.recycle.RecycleQueryModel;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.RecycleRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    RecycleRepository recycleRepository;

//    @Override
//    public Long add(RecycleAddModel model) {
//        File file = fileRepository.findById()
//        return null;
//    }

    @Override
    public List<Long> addInBatch(RecycleAddInBatchModel model) {
        List<File> files = fileRepository.findAllById(model.getFiles());
        List<Recycle> recycles = files.stream().map(s -> {
            fileService.hasAuthority(null, s.getId(), true, Umask.RECYCLE);
            s.setStatus(false);
            Recycle recycle = new Recycle();
            recycle.setCreationBy(ContextHelper.getCurrentUsername());
            recycle.setCreationTime(new Date());
            recycle.setFile(s);
            recycle.setPersonal(s.getPersonal());
            return recycle;
        }).collect(Collectors.toList());
        return recycleRepository.saveAll(recycles).stream().map(s -> s.getId()).collect(Collectors.toList());
    }

    @Override
    public List<Recycle> query(RecycleQueryModel model) {
        if (ContextHelper.getCurrentUser().hasSuperRole()) {
            return recycleRepository.findAllByPersonalIsFalseOrCreationBy(ContextHelper.getCurrentUsername())
                    .stream().filter(s -> s.getFile() != null).collect(Collectors.toList());
        }
        return recycleRepository.findAllByCreationBy(ContextHelper.getCurrentUsername())
                .stream().filter(s -> s.getFile() != null).collect(Collectors.toList());
    }

    @Override
    public Recycle get(Long id) {
        return recycleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Recycle recycle = recycleRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, id));
        fileService.delete(recycle.getFile().getId());
        recycleRepository.delete(recycle);
    }

    @Override
    public void recover(Long id) {
        Recycle recycle = recycleRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, id));
        fileService.recover(recycle.getFile().getId());
        recycleRepository.delete(recycle);
    }

    @Override
    public void deleteInBatch(List<Long> ids) {
        ids.stream().forEach(s -> delete(s));
    }

    @Override
    public void recoverInBatch(List<Long> ids) {
        ids.stream().forEach(s -> recover(s));
    }

    // inner
}
