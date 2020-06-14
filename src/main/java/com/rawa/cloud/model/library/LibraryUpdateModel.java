package com.rawa.cloud.model.library;

import com.rawa.cloud.constant.LibraryVisibility;
import com.rawa.cloud.domain.Library;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LibraryUpdateModel extends BaseModel<Library> {

//    @ApiModelProperty(value = "库名称", required = true)
//    @NotEmpty
//    private String name;

    @ApiModelProperty(value = "可见性", required = true)
    @NotNull
    private LibraryVisibility visibility;
}
