package com.rawa.cloud.model.file;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.model.common.BaseModel;
import com.rawa.cloud.model.common.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@ApiModel
@Data
public class FileSearchModel extends PageModel<File> {

    @ApiModelProperty(value = "文件名称", required = true)
    @Size(max = 80)
    private String name;

    @ApiModelProperty(value = "创建人")
    private String creationBy;

    @ApiModelProperty(value = "创建时间开始")
    private Date creationTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    private Date creationTimeEnd;

    @ApiModelProperty(value = "负责人")
    private String leader;

    @ApiModelProperty(value = "所属辖区")
    private String location;

    @ApiModelProperty(value = "所属单位")
    private String unit;

    @ApiModelProperty(value = "重点单位")
    private String keyUnit;

    @ApiModelProperty(value = "标签")
    private String tags;
}
