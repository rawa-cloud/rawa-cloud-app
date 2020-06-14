package com.rawa.cloud.model.library;

import com.rawa.cloud.constant.LibraryVisibility;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LibraryCatalogUpdateModel extends BaseModel<LibraryCatalog> {

    @ApiModelProperty(value = "模版名称", required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "可见行", required = true)
    @NotNull
    private LibraryVisibility visibility;
}
