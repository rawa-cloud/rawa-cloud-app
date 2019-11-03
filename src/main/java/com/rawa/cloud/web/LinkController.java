package com.rawa.cloud.web;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.link.LinkAddModel;
import com.rawa.cloud.model.link.LinkPatchModel;
import com.rawa.cloud.model.link.LinkQueryModel;
import com.rawa.cloud.service.LinkService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "外链相关接口")
@RestController
@RequestMapping("/links")
public class LinkController extends RestfulController<Link, LinkAddModel, LinkPatchModel, LinkQueryModel> {

    @Autowired
    LinkService linkService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody LinkAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(linkService.add(model));
    }

    @Override
    @Deprecated
    public JsonResult patch(@PathVariable  Long id, @Valid @RequestBody LinkPatchModel model, BindingResult result) {
        return null;
    }

    @Override
    public JsonResult<List<Link>> query(@Valid LinkQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(linkService.query(model));
    }

    @Override
    public JsonResult<Link> get(@PathVariable Long id) {
        return JsonResult.success(linkService.get(id));
    }

    @Override
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
