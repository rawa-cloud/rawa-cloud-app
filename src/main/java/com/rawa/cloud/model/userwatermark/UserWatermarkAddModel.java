package com.rawa.cloud.model.userwatermark;

import com.rawa.cloud.domain.UserWatermark;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UserWatermarkAddModel extends BaseModel<UserWatermark> {

    @ApiModelProperty(value = "用户名")
    @NotEmpty
    private String username;

    @ApiModelProperty(value = "水印ID")
    @NotNull
    private Long watermarkId;

    @ApiModelProperty(value = "下载时使用")
    @NotNull
    private Boolean download;

    @NotNull
    @ApiModelProperty(value = "预览时使用")
    private Boolean preview;

}
