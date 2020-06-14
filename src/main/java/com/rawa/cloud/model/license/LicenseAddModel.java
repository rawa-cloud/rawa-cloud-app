package com.rawa.cloud.model.license;

import com.rawa.cloud.bean.Licence;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@ApiModel
@Data
public class LicenseAddModel extends BaseModel<Licence> {

    @ApiModelProperty(value = "license text")
    @NotEmpty
    private String text;
}
