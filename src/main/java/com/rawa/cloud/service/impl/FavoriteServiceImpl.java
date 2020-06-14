package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.FileOptType;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.favorite.CatalogAddModel;
import com.rawa.cloud.model.favorite.ItemAddModel;
import com.rawa.cloud.repository.FavoriteCatalogRepository;
import com.rawa.cloud.repository.FavoriteItemRepository;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.FavoriteService;
import com.rawa.cloud.service.FileLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    FileLogService fileLogService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FavoriteCatalogRepository favoriteCatalogRepository;

    @Autowired
    FavoriteItemRepository favoriteItemRepository;

    @Autowired
    FileRepository fileRepository;

    @Override
    public List<FavoriteCatalog> getCatalogs() {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ACCESS_DENIED, null));
        return favoriteCatalogRepository.findAllByUser(user);
    }

    @Override
    public Long addCatalog(CatalogAddModel model) {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ACCESS_DENIED, null));
        FavoriteCatalog catalog = new FavoriteCatalog();
        catalog.setUser(user);
        catalog.setName(model.getName());
        List<FavoriteCatalog> ret = favoriteCatalogRepository.findAllByUser(user);
        boolean has = ret.stream().anyMatch(s -> s.getName().equals(model.getName()));
        if (has) throw new AppException(HttpJsonStatus.FAVORITE_CATALOG_EXISTS, model.getName());
        return favoriteCatalogRepository.save(catalog).getId();
    }

    @Override
    public void deleteCatalog(Long id) {
        favoriteCatalogRepository.deleteById(id);
    }

    @Override
    public List<FavoriteItem> getItems(Long catalogId) {
        FavoriteCatalog catalog = favoriteCatalogRepository.findById(catalogId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ERROR, "未找到收藏类别" + catalogId));
        return favoriteItemRepository.findAllByCatalog(catalog);
    }

    @Override
    public Long addItem(ItemAddModel model) {
        FavoriteCatalog catalog = favoriteCatalogRepository.findById(model.getCatalogId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ERROR, "未找到收藏类别" + model.getCatalogId()));
        File file = fileRepository.findById(model.getFileId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, model.getFileId()));
        List<FavoriteItem> ret = favoriteItemRepository.findAllByCatalog(catalog);
        boolean has = ret.stream().anyMatch(s -> s.getName().equals(model.getName()));
        if (has) throw new AppException(HttpJsonStatus.FAVORITE_ITEM_EXISTS, model.getName());
        FavoriteItem item = new FavoriteItem();
        item.setCatalog(catalog);
        item.setFile(file);
        item.setName(model.getName());
        item.setRemark(model.getRemark());
        fileLogService.add(file.getId(), FileOptType.collect, item.getName());
        return favoriteItemRepository.save(item).getId();
    }

    @Override
    public void deleteItem(Long id) {
        favoriteItemRepository.deleteById(id);
    }
}
