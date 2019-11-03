package com.rawa.cloud.model.authority;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class AuthorityQueryModel extends BaseModel<User> {

    @ApiModelProperty(value = "是否是用户权限(不指定则查全部)")
    private Boolean isUser;

    @ApiModelProperty(value = "实体ID")
    private Long principleId;

    @ApiModelProperty(value = "文件ID")
    private Long fileId;

//    @ApiModelProperty(value = "权限掩码", required = true)
//    @NotNull
//    private Long umask;
//
//    @ApiModelProperty(value = "失效时间")
//    private Date expiryTime;

}
