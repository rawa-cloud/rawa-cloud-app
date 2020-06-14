package com.rawa.cloud.repository;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Record;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface RecordRepository extends BaseRepository<Record> {
    List<Record> findAllByFileOrderByLastChangeTimeDesc(File file);
}
