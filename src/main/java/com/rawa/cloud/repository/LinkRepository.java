package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LinkRepository extends BaseRepository<Link>, JpaSpecificationExecutor<Link> {

    List<Link> findAllByCreationBy (String creationBy);

}
