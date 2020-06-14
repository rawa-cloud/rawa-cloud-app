package com.rawa.cloud.model.authority;

import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class AuthorityAddModel extends BaseModel<Authority> {

    @ApiModelProperty(value = "实体ID", required = true)
    @NotNull
    private Long principleId;

    @ApiModelProperty(value = "是否用户", required = true)
    @NotNull
    private Boolean isUser;

    @ApiModelProperty(value = "文件ID", required = true)
    @NotNull
    private Long fileId;

    @ApiModelProperty(value = "权限掩码", required = true)
    @NotNull
    private Long umask;

    @ApiModelProperty(value = "有效天数")
    private Long validDays;
}
