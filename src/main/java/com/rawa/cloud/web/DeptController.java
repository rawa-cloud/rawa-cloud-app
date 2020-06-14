package com.rawa.cloud.web;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.DeptAuthority;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.model.dept.DeptUpdateModel;
import com.rawa.cloud.service.AuthorityService;
import com.rawa.cloud.service.DeptService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "部门相关接口")
@RestController
@RequestMapping("/depts")
public class DeptController extends RestfulController<Dept, DeptAddModel, DeptUpdateModel, DeptQueryModel> {

    @Autowired
    DeptService deptService;

    @Autowired
    AuthorityService authorityService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody DeptAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(deptService.add(model));
    }

    @Override
    public JsonResult update(@PathVariable Long id, @Valid @RequestBody DeptUpdateModel model, BindingResult result) {
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

//    @ApiOperation("查询部门文件权限列表")
//    @GetMapping("/{id}/authorities")
//    public JsonResult<List<DeptAuthority>> getAuthorities (@PathVariable Long id) {
//        return JsonResult.success(authorityService.query(id, false, null));
//    }

//    @ApiOperation("新增部门文件权限")
//    @PostMapping("/{id}/authorities")
//    public JsonResult<Long> addAuthority (@PathVariable Long id, @RequestBody AuthorityAddModel model, BindingResult result) {
//        ValidationHelper.validate(result);
//        return JsonResult.success(authorityService.add(false, model));
//    }

//    @ApiOperation("局部更新部门文件权限")
//    @PatchMapping("/{id}/authorities/{authId}")
//    public JsonResult patchAuthority (@PathVariable Long authId, @RequestBody AuthorityPatchModel model, BindingResult result) {
//        ValidationHelper.validate(result);
//        authorityService.patch(authId, false, model);
//        return JsonResult.success(null);
//    }

//    @ApiOperation("删除部门文件权限")
//    @DeleteMapping("/{id}/authorities/{ids}")
//    public JsonResult deleteAuthority (@PathVariable Long id, @PathVariable String ids) {
//        List<Long> deleteIds = ContextHelper.getBatchIds(ids);
//        authorityService.delete(false, deleteIds);
//        return JsonResult.success(null);
//    }

}
