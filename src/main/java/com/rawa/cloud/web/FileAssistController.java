package com.rawa.cloud.web;

import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.fileassist.FileAssistAutoSaveRequestModel;
import com.rawa.cloud.service.FileAssistService;
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
import java.util.HashMap;
import java.util.Map;

@Api(tags = "文件辅助相关接口")
@RestController
@RequestMapping("/file-assist")
public class FileAssistController {

    @Autowired
    FileAssistService fileAssistService;

    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download (@PathVariable Long id, HttpServletResponse response) {
        java.io.File rawFile = fileAssistService.download(id);
        return FileHelper.download(rawFile, response);
    }

    @ApiOperation("自动保存")
    @PostMapping("/autoSave/{id}")
    public Map<String, Object> autoSave (@PathVariable Long id, @Valid @RequestBody FileAssistAutoSaveRequestModel model, BindingResult result) {
        ValidationHelper.validate(result);
        fileAssistService.autoSave(id, model);
        Map<String, Object> ret = new HashMap<>();
        ret.put("error", 0);
        return ret;
    }
}
