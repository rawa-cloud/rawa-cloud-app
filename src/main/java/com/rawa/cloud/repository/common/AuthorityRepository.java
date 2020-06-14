package com.rawa.cloud.repository.common;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.domain.common.BaseEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface AuthorityRepository<E extends BaseEntity, S extends Authority<E>> extends BaseRepository<S>, JpaSpecificationExecutor<S> {
    S findAuthorityByPrincipleAndFile(E principle, File file);

    List<S> findAllByFile(File file);

    S findByPrincipleAndFile(E principle, File file);

    List<S> findAllByPrinciple(E principle);

    List<S> findAllByExpiryTimeIsNotNull();

    S findByPrincipleInAndFile(Collection<E> principles, File file);
}
