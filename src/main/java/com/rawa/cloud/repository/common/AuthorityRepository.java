package com.rawa.cloud.repository.common;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.common.Authority;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AuthorityRepository<E, S extends Authority<E>> extends BaseRepository<S> {
    S findAuthorityByPrincipleAndFile(E principle, File file);

    List<S> findAllByFile(File file);

    List<S> findAllByPrincipleAndFile(E principle, File file);

    List<S> findAllByPrinciple(E principle);

    List<S> findAllByExpiryTimeIsNotNull();
}
