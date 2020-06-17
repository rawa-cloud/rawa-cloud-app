package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WatermarkRepository extends BaseRepository<Watermark>, JpaSpecificationExecutor<Watermark> {
    boolean existsByName(String name);
}
