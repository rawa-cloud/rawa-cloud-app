package com.rawa.cloud.model.recycle;

import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel
@Data
public class RecycleAddModel extends BaseModel<Recycle> {

    @ApiModelProperty(value = "回收文件ID")
    @NotEmpty
    private Long fileId;
}
