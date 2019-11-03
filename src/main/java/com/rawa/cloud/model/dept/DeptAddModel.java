package com.rawa.cloud.model.dept;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel
@Data
public class DeptAddModel extends BaseModel<Dept> {

    @ApiModelProperty(value = "父级部门ID")
    private Long parentId;

    @ApiModelProperty(value = "部门名称", required = true)
    @NotEmpty
    private String name;
}
