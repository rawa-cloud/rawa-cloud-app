package com.rawa.cloud.model.authority;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class AuthorityPatchModel extends BaseModel<User> {

//    @ApiModelProperty(value = "是否是用户权限", required = true)
//    @NotNull
//    private Boolean isUser;

    @ApiModelProperty(value = "权限掩码", required = true)
    @NotNull
    private Long umask;

    @ApiModelProperty(value = "有效天数")
    private Long validDays;

    @ApiModelProperty(value = "管理员目录")
    private Long files;
}
