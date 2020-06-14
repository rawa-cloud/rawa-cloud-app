package com.rawa.cloud.web;

import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.PerformanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "性能相关接口")
@RestController
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    PerformanceService performanceService;

    @ApiOperation("查询有效文件个数")
    @GetMapping("/file/valid")
    public JsonResult<Integer> getValidFileCount () {
        return JsonResult.success(performanceService.getValidFileCount());
    }

    @ApiOperation("查询磁盘容量 [use,total]")
    @GetMapping("/disc/volume")
    public JsonResult<List<Long>> getDiscVolume () {
        return JsonResult.success(performanceService.getDiscVolume());
    }
}
