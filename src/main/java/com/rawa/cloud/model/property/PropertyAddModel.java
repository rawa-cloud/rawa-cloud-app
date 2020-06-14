package com.rawa.cloud.model.property;

import com.rawa.cloud.domain.Property;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class PropertyAddModel extends BaseModel<Property> {

    @ApiModelProperty(value = "名称", required = true)
    @Size(max = 32)
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "值", required = true)
    @NotNull
    @Size(max = 32)
    private String value;
}
