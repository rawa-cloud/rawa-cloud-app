package com.rawa.cloud.domain;

import com.rawa.cloud.domain.common.Authority;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel
@Data
@Entity
public class UserAuthority extends Authority<User> {
}
