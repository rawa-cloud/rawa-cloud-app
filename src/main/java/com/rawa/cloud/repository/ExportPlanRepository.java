package com.rawa.cloud.repository;

import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.repository.common.BaseRepository;

public interface ExportPlanRepository extends BaseRepository<ExportPlan> {

    ExportPlan findByStatus(Boolean status);
}
