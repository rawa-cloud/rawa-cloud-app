package com.rawa.cloud.model.user;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UserQueryModel extends BaseModel<User> {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "中文名")
    private String cname;

    @ApiModelProperty(value = "状态： 有效， 无效")
    private Boolean status;

    @ApiModelProperty(value = "归属部门ID")
    private Long deptId;
}
