package com.rawa.cloud.model.authority;

import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class AuthorityQueryModel extends BaseModel<Authority> {

    @ApiModelProperty(value = "实体ID")
    private Long principleId;

    @ApiModelProperty(value = "是否用户")
    private Boolean isUser;

    @ApiModelProperty(value = "文件ID")
    private Long fileId;

    @ApiModelProperty(value = "是否是隐式权限")
    private Boolean implicit;
}
