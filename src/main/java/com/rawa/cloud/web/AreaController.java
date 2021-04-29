package com.rawa.cloud.web;

import com.rawa.cloud.domain.Area;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "辖区相关接口")
@RestController
@RequestMapping("/areas")
public class AreaController {

    @Resource
    AreaService areaService;

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<List<Area>> query () {
        return JsonResult.success(areaService.query());
    }
}
