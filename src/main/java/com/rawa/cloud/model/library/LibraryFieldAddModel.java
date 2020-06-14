package com.rawa.cloud.model.library;

import com.rawa.cloud.domain.Library;
import com.rawa.cloud.domain.LibraryField;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LibraryFieldAddModel extends BaseModel<LibraryField> {

//    @ApiModelProperty(value = "库ID", required = true)
//    @NotNull
//    private Long libraryId;

    @ApiModelProperty(value = "字段定义ID", required = true)
    private Long fieldDefId;

    @ApiModelProperty(value = "字段值")
    private String value;
}
