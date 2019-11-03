package com.rawa.cloud.service;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.user.UserAddModel;
import com.rawa.cloud.model.user.UserPatchModel;
import com.rawa.cloud.model.user.UserQueryModel;

import java.util.List;

public interface UserService {

    Long add(UserAddModel model);

    void update(Long id, UserPatchModel model);

    List<User> query(UserQueryModel model);

    User get(Long id);

    void delete(Long id);

    void changePassword(String oldPassword, String newPassword);
}