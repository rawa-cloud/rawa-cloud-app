package com.rawa.cloud.model.library;

import com.rawa.cloud.constant.LibraryFieldType;
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
public class LibraryCatalogFieldUpdateModel extends BaseModel<LibraryCatalog> {

    @ApiModelProperty(value = "模板字段名称", required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "模板字段类型", required = true)
    @NotNull
    private LibraryFieldType type;

    @ApiModelProperty(value = "可见行", required = true)
    @NotNull
    private LibraryVisibility visibility;
}
