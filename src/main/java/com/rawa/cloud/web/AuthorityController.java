package com.rawa.cloud.web;

import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.authority.AuthorityQueryModel;
import com.rawa.cloud.service.AuthorityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "文件权限相关接口")
@RestController
@RequestMapping("/authorities")
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;

    @ApiOperation("新增")
    @PostMapping("")
    public JsonResult<Long> add(@Valid @RequestBody AuthorityAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(authorityService.add(model));
    }

    @ApiOperation("更新")
    @PutMapping("/{id}")
    public JsonResult update(@PathVariable  Long id, Boolean isUser, @Valid @RequestBody AuthorityPatchModel model, BindingResult result) {
        ValidationHelper.validate(result);
        authorityService.update(id, isUser, model);
        return JsonResult.success(null);
    }

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<List<Authority>> query(@Valid AuthorityQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(authorityService.query(model));
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batch/{ids}")
    public JsonResult deleteInBatch(@PathVariable String ids, Boolean isUser) {
        List<Long> deleteIds = ContextHelper.getBatchIds(ids);
        authorityService.delete(isUser, deleteIds);
        return JsonResult.success(null);
    }

    @ApiOperation("获取")
    @GetMapping("/{id}")
    public JsonResult<Authority> get(@PathVariable Long id, boolean isUser) {
        return JsonResult.success(authorityService.get(id, isUser));
    }
}
