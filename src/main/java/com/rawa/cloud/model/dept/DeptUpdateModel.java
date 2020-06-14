package com.rawa.cloud.model.dept;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class DeptUpdateModel extends BaseModel<Dept> {

    @ApiModelProperty(value = "父级部门ID", required = true)
    @NotNull
    private Long parentId;

    @ApiModelProperty(value = "部门名称", required = true)
    @NotEmpty
    @Size(max = 16)
    private String name;
}
