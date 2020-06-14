package com.rawa.cloud.repository;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecycleRepository extends BaseRepository<Recycle> {
    Page<Recycle> findAllByCreationByOrderByCreationTimeDesc(String creationBy, Pageable pageable);

    Page<Recycle> findAllByUsernameIsNullOrCreationByOrderByCreationTimeDesc(String creationBy, Pageable pageable);

    List<Recycle> findAllByCreationBy(String creationBy);

    List<Recycle> findAllByUsernameIsNullOrCreationBy(String creationBy);

    Recycle findByFile(File file);
}
