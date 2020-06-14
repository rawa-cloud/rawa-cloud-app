package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Library;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LibraryRepository extends BaseRepository<Library>, JpaSpecificationExecutor<Library> {
    List<Library> findAllByNameAndCatalog(String name, LibraryCatalog catalog);

}
