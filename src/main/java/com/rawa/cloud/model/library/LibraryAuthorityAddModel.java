package com.rawa.cloud.model.library;

import com.rawa.cloud.constant.LibraryAuthorityOpt;
import com.rawa.cloud.domain.LibraryAuthority;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class LibraryAuthorityAddModel extends BaseModel<LibraryAuthority> {

    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty
    private String username;

    @ApiModelProperty(value = "权限标志")
    @NotNull
    private LibraryAuthorityOpt opt;
}
