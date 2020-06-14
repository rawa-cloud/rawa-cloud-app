package com.rawa.cloud.model.library;

import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LibraryCatalogFieldDictAddModel extends BaseModel<LibraryCatalog> {

    @ApiModelProperty(value = "模版字段ID", required = true)
    @NotNull
    private Long filedDefId;

    @ApiModelProperty(value = "字典名称", required = true)
    @NotEmpty
    private String name;
}
