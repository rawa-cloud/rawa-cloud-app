package com.rawa.cloud.model.recycle;

import com.rawa.cloud.domain.Recycle;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class RecycleQueryModel extends PageModel<Recycle> {

//    @ApiModelProperty(value = "创建人")
//    private String creationBy;

}
