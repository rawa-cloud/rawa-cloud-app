package com.rawa.cloud.model.library;

import com.rawa.cloud.domain.Library;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@ApiModel
@Data
public class LibraryQueryModel extends PageModel<Library> {

    @ApiModelProperty(value = "库模版ID", required = true)
    @NotNull
    private Long catalogId;

    @ApiModelProperty(value = "库名称")
    private String name;

    @ApiModelProperty(value = "动态参数")
    private Map<String, Object> params;
}
