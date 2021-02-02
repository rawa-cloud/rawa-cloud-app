package com.rawa.cloud.web;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Record;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.file.*;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "文件相关接口")
@RestController
@RequestMapping("/files")
public class FileController extends RestfulController<File, FileAddModel, FileUpdateModel, FileQueryModel> {

    @Autowired
    FileService fileService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody FileAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.add(model));
    }

    @Override
    public JsonResult update(@PathVariable Long id, @Valid @RequestBody FileUpdateModel model, BindingResult result) {
        ValidationHelper.validate(result);
        fileService.update(id, model);
        return JsonResult.success(null);
    }

    @Override
    public JsonResult<List<File>> query(@Valid FileQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.query(model));
    }

    @Override
    public JsonResult<File> get(@PathVariable Long id) {
        return JsonResult.success(fileService.get(id));
    }

    @Deprecated
    @Override
    public JsonResult delete(@PathVariable Long id) {
        return JsonResult.fail(HttpJsonStatus.ERROR, "已废弃");
    }

    @ApiOperation("获取父级列表")
    @GetMapping("/{id}/parents")
    public JsonResult<List<File>> getParents(@PathVariable Long id) {
        return JsonResult.success(fileService.getParents(id));
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batch/{ids}")
    public JsonResult deleteInBatch(@PathVariable String ids) {
        List<Long> deleteIds = ContextHelper.getBatchIds(ids);
        fileService.delete(deleteIds);
        return JsonResult.success(null);
    }


    @ApiOperation("重命名")
    @PutMapping("/{id}/rename")
    public JsonResult rename(@PathVariable Long id, @RequestBody @Valid FileRenameModel model, BindingResult result) {
        ValidationHelper.validate(result);
        fileService.rename(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("更新文件内容")
    @PutMapping("/{id}/update")
    public JsonResult renew(@PathVariable Long id, @RequestBody @Valid FileRenewModel model, BindingResult result) {
        ValidationHelper.validate(result);
        fileService.renew(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("获取根目录")
    @GetMapping("/file/root")
    public JsonResult<File> getRoot() {
        return JsonResult.success(fileService.getRootFile());
    }

    @ApiOperation("获取管理根目录")
    @GetMapping("/admin-roots")
    public JsonResult<List<File>> getAdminRoots() {
        return JsonResult.success(fileService.getAdminRoots());
    }

    @ApiOperation("获取用户根目录")
    @GetMapping("/file/user-root")
    public JsonResult<File> getUserRoot() {
        return JsonResult.success(fileService.getUserRootFile());
    }

    @ApiOperation("图片剪裁下载")
    @GetMapping("/download-image/{id}")
    public ResponseEntity<FileSystemResource> downloadImage (@PathVariable Long id, Integer height, Integer width, HttpServletResponse response) {
        java.io.File rawFile = fileService.download(id, true, height, width);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileService.download(id, true, null, null);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("预览文件")
    @GetMapping("/{id}/preview")
    public ResponseEntity<FileSystemResource> preview (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileService.preview(id);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("预览图片，设有缓存")
    @GetMapping("/preview-image/{id}")
    public ResponseEntity<FileSystemResource> previewImage (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileService.preview(id);
        return FileHelper.downloadWithCache(rawFile, response);
    }

    @ApiOperation("查询文件轨迹列表")
    @GetMapping("/{id}/records")
    public JsonResult<List<Record>> queryRecords (@PathVariable Long id) {
        return JsonResult.success(fileService.getRecords(id));
    }

    @ApiOperation("批量添加")
    @PostMapping("/batch")
    public JsonResult<List<Long>> add(@Valid @RequestBody List<FileBatchAddModel> model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.batchAdd(model));
    }

    @ApiOperation("移动")
    @PostMapping("/move")
    public JsonResult<Integer> move(@Valid @RequestBody FileMoveModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.move(model));
    }

    @ApiOperation("复制")
    @PostMapping("/copy")
    public JsonResult<Integer> copy(@Valid @RequestBody FileMoveModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.copy(model));
    }

    @ApiOperation("搜索")
    @GetMapping("/search")
    public JsonResult<Page<File>> search(@Valid FileSearchModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.search(model));
    }

    @ApiOperation("修改标签")
    @PostMapping("/{id}/tags")
    public JsonResult updateTags(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String tags = body == null ? "" : body.get("tags");
        fileService.updateTags(id, tags);
        return JsonResult.success(null);
    }

    @ApiOperation("修改文件信息")
    @PostMapping("/{id}/fileInfo")
    public JsonResult updateTags(@PathVariable Long id, @Valid @RequestBody FileInfoUpdateModel model, BindingResult result) {
        ValidationHelper.validate(result);
        fileService.updateFileInfo(id, model);
        return JsonResult.success(null);
    }
}
