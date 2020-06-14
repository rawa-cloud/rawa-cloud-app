package com.rawa.cloud.repository;

import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.repository.common.BaseRepository;

public interface ImportPlanRepository extends BaseRepository<ImportPlan> {
    ImportPlan findByStatus(Boolean status);
}
