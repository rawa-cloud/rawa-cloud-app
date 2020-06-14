package com.rawa.cloud.model.favorite;

import com.rawa.cloud.domain.FavoriteItem;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ItemQueryModel extends BaseModel<FavoriteItem> {

    @ApiModelProperty(value = "类别ID")
    private Long catalogId;
}
