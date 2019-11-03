package com.rawa.cloud.model.user;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel
@Data
public class UserAddModel extends BaseModel<User> {

    @ApiModelProperty(value = "用户名", example = "root", required = true)
    @NotEmpty
    @Size(min = 1, max = 32)
    private String username;

    @ApiModelProperty(value = "中文名称")
    private String cname;

    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty
    @Size(min = 6, max = 32)
    private String password;

    @ApiModelProperty(value = "ip段", example = "10.192.168.1-10")
    private String ip;

    @ApiModelProperty(value = "归属部门ID")
    private Long deptId;

    @ApiModelProperty(value = "角色列表", example = "['SUPER', 'ADMIN', 'USER']")
    private List<String> roles;

    @ApiModelProperty(value = "用户所管理的根目录列表 (只有 ADMIN 权限的用户可配置)")
    private List<Long> files;

    @Override
    public User toDomain() {
        User user = super.toDomain();
        user.setStatus(true);
        return user;
    }
}
