package com.rawa.cloud.domain;

import com.rawa.cloud.constant.HorizontalPosition;
import com.rawa.cloud.constant.VerticalPosition;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@ApiModel
@Data
@Entity
public class Watermark extends BaseEntity {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "logo地址")
    private String uuid;

    @ApiModelProperty(value = "水印内容")
    private String content;

    @ApiModelProperty(value = "状态：开启/关闭")
    private Boolean status;


    @ApiModelProperty(value = "水印所占宽度比例")
    @Max(1)
    @Min(0)
    private Float widthRadio;

    @ApiModelProperty(value = "logo所占宽度比例")
    @Max(1)
    @Min(0)
    private Float logoWidthRadio;

    @ApiModelProperty(value = "透明度")
    @Max(1)
    @Min(0)
    private Float opacity;

    @ApiModelProperty(value = "垂直位置")
    @NotNull
    private VerticalPosition verticalPosition;

    @ApiModelProperty(value = "水平位置")
    @NotNull
    private HorizontalPosition horizontalPosition;
}

