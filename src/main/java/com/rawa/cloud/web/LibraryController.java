package com.rawa.cloud.web;

import com.rawa.cloud.domain.Library;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.library.*;
import com.rawa.cloud.service.LibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "库相关接口")
@RestController
@RequestMapping("/libraries")
public class LibraryController {

    @Autowired
    LibraryService libraryService;

    @ApiOperation("查询库模板")
    @GetMapping("/catalogs")
    public JsonResult<List<LibraryCatalog>> getLibCatalogs () {
        return JsonResult.success(libraryService.getLibCatalogs());
    }

    @ApiOperation("获取库模板")
    @GetMapping("/catalogs/{id}")
    public JsonResult<LibraryCatalog> getLibCatalog (@PathVariable Long id) {
        return JsonResult.success(libraryService.getLibCatalog(id));
    }

    @ApiOperation("新增库模板")
    @PostMapping("/catalogs")
    public JsonResult<Long> addLibCatalog (@Valid @RequestBody LibraryCatalogAddModel model, BindingResult result) {
        ValidationHelper.validate((result));
        return JsonResult.success(libraryService.addLibraryCatalog(model));
    }

    @ApiOperation("编辑库权限")
    @PostMapping("/catalogs/{id}/authorities")
    public JsonResult updateLibraryCatalogAuthorities (@PathVariable Long id, @Valid @RequestBody List<LibraryAuthorityAddModel> model, BindingResult result) {
        ValidationHelper.validate(result);
        libraryService.updateLibraryCatalogAuthorities(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("新增库模板字段")
    @PostMapping("/catalog/fields")
    public JsonResult<Long> addLibCatalogField (@Valid @RequestBody LibraryCatalogFieldAddModel model, BindingResult result) {
        ValidationHelper.validate((result));
        return JsonResult.success(libraryService.addLibraryCatalogField(model));
    }

    @ApiOperation("新增库模板字段字典")
    @PostMapping("/catalog/field/dicts")
    public JsonResult<Long> addLibCatalogFieldDict (@Valid @RequestBody LibraryCatalogFieldDictAddModel model, BindingResult result) {
        ValidationHelper.validate((result));
        return JsonResult.success(libraryService.addLibraryCatalogFieldDict(model));
    }

    @ApiOperation("编辑库模板")
    @PutMapping("/catalogs/{id}")
    public JsonResult updateLibCatalog (@PathVariable Long id, @Valid @RequestBody LibraryCatalogUpdateModel model, BindingResult result) {
        ValidationHelper.validate((result));
        libraryService.updateLibraryCatalog(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("编辑库模板字段")
    @PutMapping("/catalog/fields/{id}")
    public JsonResult updateLibCatalogField (@PathVariable Long id, @Valid @RequestBody LibraryCatalogFieldUpdateModel model, BindingResult result) {
        ValidationHelper.validate((result));
        libraryService.updateLibraryCatalogField(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("编辑库模版字段权限")
    @PostMapping("/catalogs/fields/{id}/authorities")
    public JsonResult updateLibraryFieldDefAuthorities (@PathVariable Long id, @Valid @RequestBody List<LibraryAuthorityAddModel> model, BindingResult result) {
        ValidationHelper.validate(result);
        libraryService.updateLibraryFieldDefAuthorities(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("编辑库模板字段字典")
    @PutMapping("/catalog/field/dicts/{id}")
    public JsonResult updateLibCatalogFieldDict (@PathVariable Long id, @Valid @RequestBody LibraryCatalogFieldDictUpdateModel model, BindingResult result) {
        ValidationHelper.validate((result));
        libraryService.updateLibraryCatalogFieldDict(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("删除库模板")
    @DeleteMapping("/catalogs/{id}")
    public JsonResult deleteLibCatalog (@PathVariable Long id) {
        libraryService.deleteLibraryCatalog(id);
        return JsonResult.success(null);
    }

    @ApiOperation("删除库模板字段")
    @DeleteMapping("/catalog/fields/{id}")
    public JsonResult deleteLibCatalogField (@PathVariable Long id) {
        libraryService.deleteLibraryCatalogField(id);
        return JsonResult.success(null);
    }

    @ApiOperation("删除库模板字段字典")
    @DeleteMapping("/catalog/field/dicts/{id}")
    public JsonResult deleteLibCatalogFieldDict (@PathVariable Long id) {
        libraryService.deleteLibraryCatalogFieldDict(id);
        return JsonResult.success(null);
    }


    // Library
    @ApiOperation("查询库列表")
    @PostMapping("/query")
    public JsonResult<Page<Library>> getLibraries (@Valid @RequestBody LibraryQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(libraryService.getLibraries(model));
    }

    @ApiOperation("导出库列表")
    @PostMapping("/export")
    public ResponseEntity<FileSystemResource> exportLibraries (@Valid @RequestBody LibraryExportModel model, BindingResult result, HttpServletResponse response) {
        ValidationHelper.validate(result);
        return FileHelper.download(libraryService.exportLibraries(model), response);
    }

    @ApiOperation("新增库")
    @PostMapping("")
    public JsonResult<Long> addLibrary (@Valid @RequestBody LibraryAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(libraryService.addLibrary(model));
    }

    @ApiOperation("复制库")
    @PostMapping("/copy/{id}")
    public JsonResult<Long> copyLibrary (@PathVariable Long id) {
        return JsonResult.success(libraryService.copyLibrary(id));
    }

    @ApiOperation("编辑库")
    @PutMapping("/{id}")
    public JsonResult updateLibrary (@PathVariable Long id, @Valid @RequestBody LibraryUpdateModel model, BindingResult result) {
        ValidationHelper.validate(result);
        libraryService.updateLibrary(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("删除库")
    @DeleteMapping("/{id}")
    public JsonResult deleteLibrary (@PathVariable Long id) {
        libraryService.deleteLibrary(id);
        return JsonResult.success(null);
    }

    @ApiOperation("编辑库字段")
    @PostMapping("/{id}/fields")
    public JsonResult updateLibraryFields (@PathVariable Long id, @Valid @RequestBody List<LibraryFieldAddModel> model, BindingResult result) {
        ValidationHelper.validate(result);
        libraryService.updateLibraryFields(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("添加库文件")
    @PostMapping("/{id}/file/{fileId}")
    public JsonResult addLibraryFile (@PathVariable Long id, @PathVariable Long fileId) {
        libraryService.addLibraryFile(id, fileId);
        return JsonResult.success(null);
    }

    @ApiOperation("编辑库权限")
    @PostMapping("/{id}/authorities")
    public JsonResult updateLibraryAuthorities (@PathVariable Long id, @Valid @RequestBody List<LibraryAuthorityAddModel> model, BindingResult result) {
        ValidationHelper.validate(result);
        libraryService.updateLibraryAuthorities(id, model);
        return JsonResult.success(null);
    }


    @ApiOperation("预览关联文件")
    @GetMapping("/{id}/preview")
    public ResponseEntity<FileSystemResource> previewFile (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = libraryService.previewFile(id);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("下载关联文件")
    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> downloadFile (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = libraryService.downloadFile(id);
        return FileHelper.download(rawFile, response);
    }

}
