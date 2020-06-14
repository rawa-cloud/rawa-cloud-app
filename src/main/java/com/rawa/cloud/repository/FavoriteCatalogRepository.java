package com.rawa.cloud.repository;

import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface FavoriteCatalogRepository extends BaseRepository<FavoriteCatalog> {
    List<FavoriteCatalog> findAllByUser(User user);
}
