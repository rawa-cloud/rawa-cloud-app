package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Log;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LogRepository extends BaseRepository<Log>, JpaSpecificationExecutor<Log> {

}
