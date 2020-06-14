package com.rawa.cloud.web;

import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.recycle.RecycleAddInBatchModel;
import com.rawa.cloud.model.recycle.RecycleQueryModel;
import com.rawa.cloud.service.RecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "文件回收相关接口")
@RestController
@RequestMapping("/recycles")
public class RecycleController {

    @Autowired
    RecycleService recycleService;

//    @ApiOperation("新增")
//    @PostMapping("")
//    public JsonResult<Long> add(@Valid @RequestBody RecycleAddModel model, BindingResult result) {
//        return null;
//    }


    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<Page<Recycle>> query(@Valid RecycleQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(recycleService.query(model));
    }

    @ApiOperation("获取")
    @GetMapping("/{id}")
    public JsonResult<Recycle> get(@PathVariable Long id) {
        return JsonResult.success(recycleService.get(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable Long id) {
        recycleService.delete(id);
        return JsonResult.success(null);
    }

    @ApiOperation("恢复")
    @DeleteMapping("/recover/{id}")
    public JsonResult recover(@PathVariable Long id) {
        recycleService.recover(id);
        return JsonResult.success(null);
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batch/{ids}")
    public JsonResult deleteInBatch(@PathVariable String ids) {
        recycleService.deleteInBatch(ContextHelper.getBatchIds(ids));
        return JsonResult.success(null);
    }

    @ApiOperation("批量恢复")
    @DeleteMapping("/batch/recover/{ids}")
    public JsonResult recoverInBatch(@PathVariable String ids) {
        recycleService.recoverInBatch(ContextHelper.getBatchIds(ids));
        return JsonResult.success(null);
    }

    @ApiOperation("清空回收站")
    @DeleteMapping("/clear")
    public JsonResult clear() {
        recycleService.clear();
        return JsonResult.success(null);
    }
}
