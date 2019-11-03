package com.rawa.cloud.model.recycle;

import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Data
public class RecycleAddInBatchModel extends BaseModel<Recycle> {

    @ApiModelProperty(value = "回收文件ID列表")
    @NotNull
    private List<Long> files;
}
