package com.rawa.cloud.repository;

import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserWatermarkRepository extends BaseRepository<UserWatermark>, JpaSpecificationExecutor<UserWatermark> {
    boolean existsByUsernameAndWatermark(String user, Watermark watermark);

    void deleteAllByWatermark(Watermark watermark);

    UserWatermark findByUsername(String username);
}
