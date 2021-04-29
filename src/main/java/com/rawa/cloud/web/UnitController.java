package com.rawa.cloud.web;

import com.rawa.cloud.domain.Area;
import com.rawa.cloud.domain.Unit;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.AreaService;
import com.rawa.cloud.service.UnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "重点单位相关接口")
@RestController
@RequestMapping("/units")
public class UnitController {

    @Resource
    UnitService unitService;

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<List<Unit>> query () {
        return JsonResult.success(unitService.query());
    }
}
