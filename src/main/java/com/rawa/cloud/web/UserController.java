package com.rawa.cloud.web;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.user.*;
import com.rawa.cloud.service.UserService;
import com.rawa.cloud.web.common.RestfulController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
public class UserController extends RestfulController<User, UserAddModel, UserUpdateModel, UserQueryModel> {

    @Autowired
    UserService userService;

    @Override
    public JsonResult<Long> add(@Valid @RequestBody UserAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userService.add(model));
    }

    @Override
    public JsonResult update(@PathVariable  Long id, @Valid @RequestBody UserUpdateModel model, BindingResult result) {
        userService.update(id, model);
        return JsonResult.success(null);
    }

    @Override
    public JsonResult<List<User>> query(@Valid UserQueryModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userService.query(model));
    }

    @Override
    public JsonResult<User> get(@PathVariable Long id) {
        return JsonResult.success(userService.get(id));
    }

    @Override
    public JsonResult delete(@PathVariable Long id) {
        userService.delete(id);
        return JsonResult.success(null);
    }

    @ApiOperation("添加用户管理文件夹")
    @PostMapping("/{id}/files")
    public JsonResult<List<Long>> addUserFiles(@PathVariable Long id, @Valid @RequestBody UserFilesAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userService.addUserFiles(id, model));
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public JsonResult<Page<User>> queryForPage(@Valid UserQueryPageModel model, BindingResult result) {
        ValidationHelper.validate(result);
        return JsonResult.success(userService.queryForPage(model));
    }
}
