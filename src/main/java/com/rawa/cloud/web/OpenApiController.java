package com.rawa.cloud.web;

import com.rawa.cloud.domain.Area;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.dto.OpenApiFile;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.log.LogQueryModel;
import com.rawa.cloud.model.openapi.OpenApiFileParams;
import com.rawa.cloud.service.AreaService;
import com.rawa.cloud.service.NasService;
import com.rawa.cloud.service.OpenApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "开放Api相关接口")
@RestController
@RequestMapping("/openapi")
public class OpenApiController {

    @Resource
    OpenApiService openApiService;

    @Resource
    NasService nasService;

    @ApiOperation("分页查询文件列表")
    @GetMapping("/files")
    public JsonResult<Page<OpenApiFile>> queryFiles(@Valid OpenApiFileParams model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(openApiService.queryFiles(model));
    }

    @ApiOperation("根据uuid下载文件")
    @GetMapping("/files/{uuid}/download")
    public ResponseEntity<FileSystemResource> download (@PathVariable String uuid, HttpServletResponse response) {
        java.io.File rawFile = nasService.download(uuid, true);
        return FileHelper.download(rawFile, response);
    }
}
