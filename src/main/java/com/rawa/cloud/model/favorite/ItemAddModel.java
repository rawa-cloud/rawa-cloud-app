package com.rawa.cloud.model.favorite;

import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class ItemAddModel extends BaseModel<FavoriteItem> {

    @ApiModelProperty(value = "收藏名称", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "所属类别ID")
    @NotNull
    private Long catalogId;

    @ApiModelProperty(value = "收藏文件", required = true)
    @NotNull
    private Long fileId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
