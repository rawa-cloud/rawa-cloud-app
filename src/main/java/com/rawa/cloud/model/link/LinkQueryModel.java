package com.rawa.cloud.model.link;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class LinkQueryModel extends PageModel<Link> {

//    @ApiModelProperty(value = "创建人")
//    private String creationBy;

}
