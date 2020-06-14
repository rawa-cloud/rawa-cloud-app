package com.rawa.cloud.service;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.model.recycle.RecycleAddInBatchModel;
import com.rawa.cloud.model.recycle.RecycleQueryModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecycleService {

//    Long add(RecycleAddModel model);

    List<Long> addInBatch(List<File> files);

//    void update(Long id, FileUpdateModel model);

    Page<Recycle> query(RecycleQueryModel model);

    Recycle get(Long id);

    void delete(Long id);

    void recover (Long id);

    void deleteInBatch(List<Long> ids);

    void recoverInBatch (List<Long> ids);

    void clear ();
}