package com.rawa.cloud.web;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptPatchModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.service.DeptService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "部门相关接口")
@RestController
@RequestMapping("/depts")
public class DeptController extends RestfulController<Dept, DeptAddModel, DeptPatchModel, DeptQueryModel> {

    @Autowired
    DeptService deptService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody DeptAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(deptService.add(model));
    }

    @Override
    public JsonResult patch(@PathVariable Long id, @Valid @RequestBody DeptPatchModel model, BindingResult result) {
        ValidationHelper.validate(result);
        deptService.update(id, model);
        return JsonResult.success(null);
    }

    @Override
    public JsonResult<List<Dept>> query(@Valid DeptQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(deptService.query(model));
    }

    @Override
    public JsonResult<Dept> get(@PathVariable Long id) {
        return JsonResult.success(deptService.get(id));
    }

    @Override
    public JsonResult delete(@PathVariable Long id) {
        deptService.delete(id);
        return JsonResult.success(null);
    }

}
