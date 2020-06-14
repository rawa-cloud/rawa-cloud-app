package com.rawa.cloud.web;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.auth.ChangePasswordModel;
import com.rawa.cloud.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "认证相关接口")
@RestController
public class AuthController {

    @Autowired
    UserService userService;

    /**
     * 这是一个形式接口，真正处理逻辑由 Spring Security 管理
     * 请参见 {@link com.rawa.cloud.configuration.WebSecurityConfig}
     * @return
     */
    @ApiOperation("登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", defaultValue = "user"),
            @ApiImplicitParam(name = "password", defaultValue = "123456"),
    })
    @PostMapping("/login")
    public JsonResult login (String username, String password) {
        return JsonResult.success(null);
    }

    /**
     * 这是一个形式接口，真正处理逻辑由 Spring Security 管理
     * 请参见 {@link com.rawa.cloud.configuration.WebSecurityConfig}
     * @return
     */
    @ApiOperation("登出")
    @PostMapping("/logout")
    public JsonResult logout () {
        return JsonResult.success(null);
    }

    @ApiOperation("获取登录用户信息")
    @GetMapping("/auth/principle")
    public JsonResult<User> getUser () {
        return JsonResult.success(ContextHelper.getCurrentUser());
    }

    @ApiOperation("更改密码")
    @PutMapping("/auth/password")
    public JsonResult<User> changePassword (@Valid @RequestBody ChangePasswordModel model, BindingResult result) {
        ValidationHelper.validate(result);
        userService.changePassword(model.getOldPassword(), model.getNewPassword());
        return JsonResult.success(null);
    }
}
