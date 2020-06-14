package com.rawa.cloud.repository;

import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.domain.LibraryFieldDef;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface LibraryFieldDefRepository extends BaseRepository<LibraryFieldDef> {

    List<LibraryFieldDef> findAllByNameAndCatalog(String name, LibraryCatalog catalog);
}
