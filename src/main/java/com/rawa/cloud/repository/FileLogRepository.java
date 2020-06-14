package com.rawa.cloud.repository;

import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FileLogRepository extends BaseRepository<FileLog> {
    Page<FileLog> findAllByFileIdOrderByOptTimeDesc(Long fileId, Pageable pageable);

    int countAllByFileIdAndType(Long fileId, FileOptType type);
}
