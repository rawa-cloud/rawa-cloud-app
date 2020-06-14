package com.rawa.cloud.service;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.model.user.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    Long add(UserAddModel model);

    void update(Long id, UserUpdateModel model);

    List<User> query(UserQueryModel model);

    Page<User> queryForPage(UserQueryPageModel model);

    User get(Long id);

    void delete(Long id);

    void changePassword(String oldPassword, String newPassword);

    List<Long> addUserFiles (Long id, UserFilesAddModel model);
}