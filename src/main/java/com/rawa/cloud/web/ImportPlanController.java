package com.rawa.cloud.web;

import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.importplan.ImportPlanAddModel;
import com.rawa.cloud.service.ImportPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "导入计划相关接口")
@RestController
@RequestMapping("/import-plans")
public class ImportPlanController {

    @Autowired
    ImportPlanService importPlanService;

    @ApiOperation("添加")
    @PostMapping()
    public JsonResult<Long> add(@Valid @RequestBody ImportPlanAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(importPlanService.addPlan(model));
    }

    @ApiOperation("获取")
    @GetMapping("/active")
    public JsonResult<ImportPlan> get() {
        return JsonResult.success(importPlanService.getPlan());
    }

    @ApiOperation("删除")
    @DeleteMapping("/active")
    public JsonResult delete() {
        importPlanService.deletePlan();
        return JsonResult.success(null);
    }

    @ApiOperation("获取导入文件目录")
    @GetMapping("/files")
    public JsonResult<List<String>> getImportFileList() {
        return JsonResult.success(importPlanService.getImportFileList());
    }
}
