package com.rawa.cloud.web;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.file.FileAddModel;
import com.rawa.cloud.model.file.FilePatchModel;
import com.rawa.cloud.model.file.FileQueryModel;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "文件相关接口")
@RestController
@RequestMapping("/files")
public class FileController extends RestfulController<File, FileAddModel, FilePatchModel, FileQueryModel> {

    @Autowired
    FileService fileService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody FileAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileService.add(model));
    }

    @Override
    public JsonResult patch(@PathVariable  Long id, @Valid @RequestBody FilePatchModel model, BindingResult result) {
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

    @Override
    @Deprecated
    public JsonResult delete(@PathVariable Long id) {
        return JsonResult.fail(HttpJsonStatus.ERROR, "已废弃");
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileService.download(id);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{id}")
    public ResponseEntity<FileSystemResource> preview (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileService.preview(id);
        return FileHelper.download(rawFile, response);
    }
}
