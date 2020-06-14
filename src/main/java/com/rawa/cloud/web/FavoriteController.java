package com.rawa.cloud.web;

import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.favorite.CatalogAddModel;
import com.rawa.cloud.model.favorite.ItemAddModel;
import com.rawa.cloud.service.FavoriteService;
import com.rawa.cloud.service.NasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "我的收藏相关接口")
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    FavoriteService favoriteService;

    @ApiOperation("获取类别列表")
    @GetMapping("/user/catalogs")
    public JsonResult<List<FavoriteCatalog>> getCatalogs () {
        return JsonResult.success(favoriteService.getCatalogs());
    }

    @ApiOperation("新增类别")
    @PostMapping("/user/catalogs")
    public JsonResult<Long> addCatalog (@Valid @RequestBody CatalogAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(favoriteService.addCatalog(model));
    }

    @ApiOperation("删除类别")
    @DeleteMapping("/user/catalogs/{id}")
    public JsonResult deleteCatalog (@PathVariable Long id) {
        favoriteService.deleteCatalog(id);
        return JsonResult.success(null);
    }

    @ApiOperation("查询收藏")
    @GetMapping("/user/items")
    public JsonResult<List<FavoriteItem>> queryItems (Long catalogId) {
        return JsonResult.success(favoriteService.getItems(catalogId));
    }

    @ApiOperation("新增收藏")
    @PostMapping("/user/items")
    public JsonResult<Long> addItem (@Valid @RequestBody ItemAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(favoriteService.addItem(model));
    }

    @ApiOperation("删除收藏")
    @DeleteMapping("/user/items/{id}")
    public JsonResult deleteItem (@PathVariable Long id) {
        favoriteService.deleteItem(id);
        return JsonResult.success(null);
    }
}
