package com.rawa.cloud.model.userwatermark;

import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UserWatermarkQueryModel extends PageModel<UserWatermark> {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "水印ID")
    private Long watermarkId;

    @ApiModelProperty(value = "下载时使用")
    private Boolean download;

    @ApiModelProperty(value = "预览时使用")
    private Boolean preview;

}
