package com.rawa.cloud.model.favorite;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.FavoriteCatalog;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel
@Data
public class CatalogAddModel extends BaseModel<FavoriteCatalog> {

    @ApiModelProperty(value = "类别名称", required = true)
    @NotNull
    private String name;
}
