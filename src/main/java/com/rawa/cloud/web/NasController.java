package com.rawa.cloud.web;

import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.NasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "文件储存相关接口")
@RestController
@RequestMapping("/nas")
public class NasController {

    @Autowired
    NasService nasService;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public JsonResult<String> upload (MultipartFile file) {
        return JsonResult.success(nasService.upload(file));
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{uuid}")
    public ResponseEntity<FileSystemResource> download (@PathVariable String uuid, HttpServletResponse response) {
        java.io.File rawFile = nasService.download(uuid, true);
        return FileHelper.download(rawFile, response);
    }

//    @ApiOperation("预览文件")
//    @GetMapping("/preview/{id}")
//    public ResponseEntity<FileSystemResource> preview (@PathVariable Long id) {
//        return null;
//    }

    @ApiOperation("删除文件")
    @DeleteMapping("/remove/{uuid}")
    public JsonResult delete (@PathVariable String uuid, HttpServletResponse response) {
        nasService.delete(uuid, false);
        return JsonResult.success(null);
    }
}
