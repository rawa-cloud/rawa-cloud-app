package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Unit;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface UnitRepository extends BaseRepository<Unit> {
    List<Unit> findAllByCodeIn(List<String> codes);
}
