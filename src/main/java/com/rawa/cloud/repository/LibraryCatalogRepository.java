package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LibraryCatalogRepository extends BaseRepository<LibraryCatalog>, JpaSpecificationExecutor<LibraryCatalog> {
    List<LibraryCatalog> findAllByName (String name);
}
