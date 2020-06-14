package com.rawa.cloud.web;

import com.rawa.cloud.domain.Log;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.log.LogQueryModel;
import com.rawa.cloud.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "操作日志相关接口")
@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    LogService logService;


//    @Deprecated
//    @ApiOperation("查询")
//    @GetMapping("")
//    public JsonResult<List<Log>> query(@Valid LogQueryModel model, BindingResult result) {
//        ValidationHelper.validate(result);
//        return JsonResult.success(logService.query(model));
//    }

    @Deprecated
    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<Page<Log>> query(@Valid LogQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(logService.query(model));
    }

}
