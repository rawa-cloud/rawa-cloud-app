package com.rawa.cloud.model.user;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel
@Data
public class UserFilesAddModel extends BaseModel<User> {

    @ApiModelProperty(value = "文件夹列表", required = true)
    @NotEmpty
    private List<Long> files;

//    @Override
//    public User toDomain() {
//        User user = super.toDomain();
//        user.setStatus(true);
//        return user;
//    }
}
