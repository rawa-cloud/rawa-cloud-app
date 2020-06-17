package com.rawa.cloud.model.watermark;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.domain.Watermark;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel
@Data
public class WatermarkUpdateModel extends BaseModel<Watermark> {
    @ApiModelProperty(value = "logo地址")
    private String uuid;

    @ApiModelProperty(value = "水印内容")
    private String content;

    @ApiModelProperty(value = "状态：开启/关闭")
    @NotNull
    private Boolean status;
}
