package com.rawa.cloud.web;

import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.userwatermark.UserWatermarkUpdateModel;
import com.rawa.cloud.model.watermark.WatermarkAddModel;
import com.rawa.cloud.model.watermark.WatermarkUpdateModel;
import com.rawa.cloud.service.WatermarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "水印相关接口")
@RestController
@RequestMapping("/watermarks")
public class WatermarkController {

    @Autowired
    WatermarkService watermarkService;

    @ApiOperation("添加")
    @PostMapping("")
    public JsonResult<Long> add(@Valid @RequestBody WatermarkAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(watermarkService.add(model));
    }

    @ApiOperation("更新")
    @PutMapping("/{id}")
    public JsonResult update(@PathVariable Long id, @Valid @RequestBody WatermarkUpdateModel model, BindingResult result) {
        ValidationHelper.validate(result);
        watermarkService.update(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<List<Watermark>> query() {
        return JsonResult.success(watermarkService.query());
    }

    @ApiOperation("获取")
    @GetMapping("{id}")
    public JsonResult<Watermark> get(@PathVariable Long id) {
        return JsonResult.success(watermarkService.get(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("")
    public JsonResult delete(@PathVariable Long id) {
        watermarkService.delete(id);
        return JsonResult.success(null);
    }
}
