package com.rawa.cloud.repository;

import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface FavoriteItemRepository extends BaseRepository<FavoriteItem> {
    List<FavoriteItem> findAllByCatalog(FavoriteCatalog catalog);
}
