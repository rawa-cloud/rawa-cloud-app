package com.rawa.cloud.web;

import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.exportplan.ExportPlanAddModel;
import com.rawa.cloud.service.ExportPlanService;
import com.rawa.cloud.service.NasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "导出计划相关接口")
@RestController
@RequestMapping("/export-plans")
public class ExportPlanController {

    @Autowired
    ExportPlanService exportPlanService;

    @ApiOperation("添加")
    @PostMapping()
    public JsonResult<Long> add(@Valid @RequestBody ExportPlanAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(exportPlanService.addPlan(model));
    }

    @ApiOperation("获取")
    @GetMapping("/active")
    public JsonResult<ExportPlan> get() {
        return JsonResult.success(exportPlanService.getPlan());
    }

    @ApiOperation("删除")
    @DeleteMapping("/active")
    public JsonResult delete() {
        exportPlanService.deletePlan();
        return JsonResult.success(null);
    }

    @ApiOperation("获取导出文件目录")
    @GetMapping("/files")
    public JsonResult<List<String>> getExportFileList() {
        return JsonResult.success(exportPlanService.getExportFileList());
    }
}
