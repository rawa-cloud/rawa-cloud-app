package com.rawa.cloud.service;

import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.authority.AuthorityQueryModel;

import java.util.List;

public interface AuthorityService {

    boolean hasAuthority(Long principleId, Long fileId, boolean isUser, Umask ...umasks);

    Long umask(Long principleId, Long fileId, boolean isUser, boolean implicit);

    Long add (AuthorityAddModel model);

    void delete (boolean isUser, List<Long> ids);

    void update(Long id, boolean isUser, AuthorityPatchModel model);

    <T extends Authority> List<T> query(AuthorityQueryModel model);

    Authority get(Long principleId, boolean isUser, Long fileId);

    Authority get(Long id, boolean isUser);
}