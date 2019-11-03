package com.rawa.cloud.model.dept;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel
@Data
public class DeptPatchModel extends BaseModel<User> {

    @ApiModelProperty(value = "父级部门ID")
    private Long parentId;

    @ApiModelProperty(value = "部门名称")
    @NotEmpty
    private String name;
}
