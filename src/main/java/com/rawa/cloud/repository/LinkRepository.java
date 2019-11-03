package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface LinkRepository extends BaseRepository<Link> {

    List<Link> findAllByCreationBy (String creationBy);

}
