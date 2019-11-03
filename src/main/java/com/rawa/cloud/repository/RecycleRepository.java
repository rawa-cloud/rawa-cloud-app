package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface RecycleRepository extends BaseRepository<Recycle> {
    List<Recycle> findAllByCreationBy(String creationBy);

    List<Recycle> findAllByPersonalIsFalseOrCreationBy(String creationBy);
}
