package com.rawa.cloud.model.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class ChangePasswordModel {

    @ApiModelProperty(value = "旧密码", required = true)
    @NotEmpty
    @Size(min = 6, max = 32)
    private String newPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @Size(min = 6, max = 32)
    @NotEmpty
    private String oldPassword;

}
