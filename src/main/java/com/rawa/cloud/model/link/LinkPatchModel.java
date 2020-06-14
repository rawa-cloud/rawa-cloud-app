package com.rawa.cloud.model.link;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class LinkPatchModel extends BaseModel<Link> {

    @ApiModelProperty(value = "密码")
    private String password;

//    @ApiModelProperty(value = "创建人")
//    private String creationBy;

//    @ApiModelProperty(value = "创建时间")
//    private Date creationTime;

    @ApiModelProperty(value = "失效时间")
    private Date expiryTime;


//    @ApiModelProperty(value = "外链文件列表", required = true)
//    @NotEmpty
//    private List<Long> files;
}
