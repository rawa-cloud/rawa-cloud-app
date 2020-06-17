package com.rawa.cloud.web;

import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.userwatermark.UserWatermarkAddModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkQueryModel;
import com.rawa.cloud.model.userwatermark.UserWatermarkUpdateModel;
import com.rawa.cloud.service.UserWatermarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "用户水印相关接口")
@RestController
@RequestMapping("/user-watermarks")
public class UserWatermarkController {

    @Autowired
    UserWatermarkService userWatermarkService;

    @ApiOperation("添加")
    @PostMapping("")
    public JsonResult<Long> add(@Valid @RequestBody UserWatermarkAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userWatermarkService.add(model));
    }

    @ApiOperation("更新")
    @PutMapping("/{id}")
    public JsonResult update(@PathVariable Long id, @Valid @RequestBody UserWatermarkUpdateModel model, BindingResult result) {
        ValidationHelper.validate(result);
        userWatermarkService.update(id, model);
        return JsonResult.success(null);
    }

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<Page<UserWatermark>> query(@Valid UserWatermarkQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userWatermarkService.query(model));
    }

    @ApiOperation("获取")
    @GetMapping("{id}")
    public JsonResult<UserWatermark> get(@PathVariable Long id) {
        return JsonResult.success(userWatermarkService.get(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("")
    public JsonResult delete(@PathVariable Long id) {
        userWatermarkService.delete(id);
        return JsonResult.success(null);
    }
}
