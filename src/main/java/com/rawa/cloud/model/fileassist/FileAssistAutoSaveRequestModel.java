package com.rawa.cloud.model.fileassist;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Data
public class FileAssistAutoSaveRequestModel {
    @NotNull
    Integer status;

    @NotNull
    String key;

    String url;

    List<String> users;
}
