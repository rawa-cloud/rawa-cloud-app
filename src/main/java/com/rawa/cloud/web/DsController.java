package com.rawa.cloud.web;

import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.DsSyncService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "同步相关接口")
@RestController
@RequestMapping("/ds")
public class DsController {

    @Resource
    DsSyncService dsSyncService;

    @GetMapping("/sync")
    public JsonResult syncData () {
        dsSyncService.syncData();
        return JsonResult.success(null);
    }

}
