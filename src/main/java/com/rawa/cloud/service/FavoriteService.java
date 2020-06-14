package com.rawa.cloud.service;

import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.model.favorite.CatalogAddModel;
import com.rawa.cloud.model.favorite.ItemAddModel;

import java.util.List;

public interface FavoriteService {

    List<FavoriteCatalog> getCatalogs();

    Long addCatalog(CatalogAddModel model);

    void deleteCatalog(Long id);

    List<FavoriteItem> getItems(Long catalogId);

    Long addItem(ItemAddModel model);

    void deleteItem(Long id);

}