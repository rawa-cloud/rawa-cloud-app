package com.rawa.cloud.model.openapi;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class OpenApiFileParams extends PageModel<File> {
    private String keyUnitId;

    private Date modifiedTimeStart;

    private Date modifiedTimeEnd;
}
