package com.rawa.cloud.service;

import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.authority.AuthorityQueryModel;

import java.util.List;

public interface AuthorityService {

    boolean hasAuthority(Long principleId, Long fileId, boolean isUser, Umask ...umasks);

    void deleteByFile(File file);

    Long add (AuthorityAddModel model);

    Authority get (Long id, boolean isUser);

    void delete (Long id, boolean isUser);

    void update(Long id, boolean isUser, AuthorityPatchModel model);

    List<Authority> query(AuthorityQueryModel model);
}