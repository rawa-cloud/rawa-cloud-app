package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Nas;
import com.rawa.cloud.repository.common.BaseRepository;

public interface NasRepository extends BaseRepository<Nas> {

    Nas findNasByUuidAndCreationBy (String uuid, String creationBy);

    Nas findNasByUuid (String uuid);

}
