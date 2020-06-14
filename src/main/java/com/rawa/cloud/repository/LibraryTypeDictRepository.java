package com.rawa.cloud.repository;

import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.domain.LibraryFieldDef;
import com.rawa.cloud.domain.LibraryTypeDict;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface LibraryTypeDictRepository extends BaseRepository<LibraryTypeDict> {

    List<LibraryTypeDict> findAllByNameAndFieldDef(String name, LibraryFieldDef fieldDef);
}
