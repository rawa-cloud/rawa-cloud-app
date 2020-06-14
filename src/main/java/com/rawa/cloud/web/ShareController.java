package com.rawa.cloud.web;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Link;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "分享相关接口")
@RestController
@RequestMapping("/shares")
public class ShareController {

    @Autowired
    ShareService shareService;

    @ApiOperation("获取链接")
    @GetMapping("/{linkId}")
    public JsonResult<Link> getLink (@PathVariable Long linkId) {
        return JsonResult.success(shareService.getLink(linkId));
    }

    @ApiOperation("查询分享文件列表")
    @GetMapping("/{linkId}/files")
    public JsonResult<List<File>> queryFiles (@PathVariable Long linkId, String password, Long fileParentId) {
        return JsonResult.success(shareService.queryFiles(linkId, password, fileParentId));
    }

    @ApiOperation("获取分享文件")
    @GetMapping("/{linkId}/files/{fileId}")
    public JsonResult<File> getFile (@PathVariable Long linkId, @PathVariable Long fileId, String password) {
        return JsonResult.success(shareService.getFile(linkId, fileId, password));
    }

    @ApiOperation("预览分享文件")
    @GetMapping("/{linkId}/files/{fileId}/preview")
    public ResponseEntity<FileSystemResource> previewFile (@PathVariable Long linkId, @PathVariable Long fileId, String password, HttpServletResponse response) {
        java.io.File rawFile = shareService.previewFile(linkId, fileId, password);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("下载分享文件")
    @GetMapping("/{linkId}/files/{fileId}/download")
    public ResponseEntity<FileSystemResource> downloadFile (@PathVariable Long linkId, @PathVariable Long fileId, String password, HttpServletResponse response) {
        java.io.File rawFile = shareService.downloadFile(linkId, fileId, password);
        return FileHelper.download(rawFile, response);
    }

}
