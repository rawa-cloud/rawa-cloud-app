package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.recycle.RecycleAddInBatchModel;
import com.rawa.cloud.model.recycle.RecycleQueryModel;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.RecycleRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.LogService;
import com.rawa.cloud.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    FileService fileService;

    @Autowired
    LogService logService;

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
    public List<Long> addInBatch(List<File> files) {
        List<Recycle> recycles = files.stream()
                .map(s -> {
                   Recycle r = new Recycle();
                   r.setFile(s);
                   r.setCreationBy(ContextHelper.getCurrentUsername());
                   r.setCreationTime(new Date());
                   r.setUsername(s.getUser() != null ? s.getUser().getUsername() : null);
                   return r;
                }).collect(Collectors.toList());
        return recycleRepository.saveAll(recycles)
                .stream().map(s -> s.getId()).collect(Collectors.toList());
    }

    @Override
    public Page<Recycle> query(RecycleQueryModel model) {
        if (ContextHelper.getCurrentUser().hasSuperRole()) {
            return recycleRepository.findAllByUsernameIsNullOrCreationByOrderByCreationTimeDesc(ContextHelper.getCurrentUsername(), model.toPage());
        }
        return recycleRepository.findAllByCreationByOrderByCreationTimeDesc(ContextHelper.getCurrentUsername(), model.toPage());
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
        logService.add(Log.build(LogModule.RECYCLE, LogType.DELETE).lc(recycle.getFile().getPath()).end());
        List<File> delFiles = new LinkedList<>();
        delFiles.add(recycle.getFile());
        traverseFile(recycle.getFile().getChildren(), s -> {
            Recycle r = recycleRepository.findByFile(s);
            if (r != null) recycleRepository.delete(r);
            delFiles.add(s);
            return null;
        });
        fileRepository.deleteInBatch(delFiles);
        recycleRepository.delete(recycle);
    }

    @Override
    @Transactional
    public void recover(Long id) {
        Recycle recycle = recycleRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.RECORD_NOT_FOUND, id));
        File file = recycle.getFile();
        if (file.getParent() == null || !Boolean.TRUE.equals(file.getParent().getStatus()))
            throw new AppException(HttpJsonStatus.FILE_DIR_NOT_FOUND, file.getParent().getName());
        if (file.getParent().exists(file.getName())) throw new AppException(HttpJsonStatus.FILE_EXIST, file.getName());
        file.setStatus(true);
        logService.add(Log.build(LogModule.RECYCLE, LogType.DELETE).lc(recycle.getFile().getPath()).st("还原").end());
        traverseFile(file.getChildren(), s -> {
            Recycle r = recycleRepository.findByFile(s);
            if (r != null) recycleRepository.delete(r);
            s.setStatus(true);
            fileRepository.save(s);
            return null;
        });
        recycleRepository.delete(recycle);
    }

    @Override
    @Transactional
    public void deleteInBatch(List<Long> ids) {
        ids.stream().forEach(s -> delete(s));
    }

    @Override
    @Transactional
    public void recoverInBatch(List<Long> ids) {
        ids.stream().forEach(s -> recover(s));
    }

    @Override
    @Transactional
    public void clear() {
        List<Recycle> recycles = ContextHelper.getCurrentUser().hasSuperRole() ?
                recycleRepository.findAllByUsernameIsNullOrCreationBy(ContextHelper.getCurrentUsername())
                : recycleRepository.findAllByCreationBy(ContextHelper.getCurrentUsername());
        if (!CollectionUtils.isEmpty(recycles)) {
            List<File> delFiles = new LinkedList<>();
            for(Recycle r: recycles) {
                recycleRepository.delete(r);
                File f = r.getFile();
                if (f != null) {
                    delFiles.add(f);
                    traverseFile(f.getChildren(), s -> {
                        delFiles.add(s);
                        return null;
                    });
                }
            }
            recycleRepository.deleteInBatch(recycles);
            fileRepository.deleteInBatch(delFiles);
            logService.add(Log.build(LogModule.RECYCLE, LogType.DELETE).st("清空回收站").end());
        }
    }


    // inner

    private void traverseFile (List<File> list, Function<File, Void> fn) {
        if (CollectionUtils.isEmpty(list)) return;
        for(File f : list) {
            traverseFile(f.getChildren(), fn);
            fn.apply(f);
        }
    }
}
