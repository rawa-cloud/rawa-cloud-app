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
import java.util.List;

@ApiModel
@Data
public class WatermarkAddModel extends BaseModel<Watermark> {

    @ApiModelProperty(value = "名称")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "logo地址")
    private String uuid;

    @ApiModelProperty(value = "水印内容")
    private String content;

}
