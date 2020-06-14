package com.rawa.cloud.web;

import com.rawa.cloud.domain.FileLog;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.filelog.FileLogCountModel;
import com.rawa.cloud.model.filelog.FileLogQueryModel;
import com.rawa.cloud.model.log.LogQueryModel;
import com.rawa.cloud.service.FileLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "文件操作日志相关接口")
@RestController
@RequestMapping("/fileLogs")
public class FileLogController {

    @Autowired
    FileLogService fileLogService;


    @Deprecated
    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<Page<FileLog>> query(@Valid FileLogQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileLogService.query(model));
    }

    @Deprecated
    @ApiOperation("统计")
    @PostMapping("/count")
    public JsonResult<Integer> count(@Valid @RequestBody FileLogCountModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(fileLogService.count(model.getFileId(), model.getType()));
    }

}
