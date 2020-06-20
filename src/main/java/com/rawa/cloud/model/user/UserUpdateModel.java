package com.rawa.cloud.model.user;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class UserUpdateModel extends BaseModel<User> {

    @ApiModelProperty(value = "中文名称")
    private String cname;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "ip段", example = "10.192.168.1-10")
    private String ip;

    @ApiModelProperty(value = "状态： 有效， 无效")
    private Boolean status;

    @ApiModelProperty(value = "归属部门ID")
    private Long deptId;

    @ApiModelProperty(value = "角色列表", example = "['SUPER', 'ADMIN', 'USER']")
    private List<String> roles;

    @ApiModelProperty(value = "用户所管理的根目录列表 (只有 ADMIN 权限的用户可配置)", example = "['SUPER', 'ADMIN', 'USER']")
    private List<Long> files;

}
