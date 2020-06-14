package com.rawa.cloud.web;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.link.LinkAddModel;
import com.rawa.cloud.model.link.LinkQueryModel;
import com.rawa.cloud.model.link.LinkUpdateModel;
import com.rawa.cloud.service.LinkService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "外链相关接口")
@RestController
@RequestMapping("/links")
public class LinkController {

    @Autowired
    LinkService linkService;

    @ApiOperation("添加")
    @PostMapping("")
    public JsonResult<Long> add(@Valid @RequestBody LinkAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(linkService.add(model));
    }

    @ApiOperation("分页查询")
    @GetMapping("")
    public JsonResult<Page<Link>> query(@Valid LinkQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(linkService.query(model));
    }

    @ApiOperation("获取")
    @GetMapping("{id}")
    public JsonResult<Link> get(@PathVariable Long id) {
        return JsonResult.success(linkService.get(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("")
    public JsonResult delete(@PathVariable Long id) {
        linkService.delete(id);
        return JsonResult.success(null);
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batch/{ids}")
    public JsonResult deleteInBatch(@PathVariable String ids) {
        linkService.deleteInBatch(ContextHelper.getBatchIds(ids));
        return JsonResult.success(null);
    }

}
