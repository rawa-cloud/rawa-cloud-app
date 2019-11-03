package com.rawa.cloud.repository;

import com.rawa.cloud.domain.User;
import com.rawa.cloud.domain.UserAuthority;
import com.rawa.cloud.repository.common.AuthorityRepository;

public interface UserAuthorityRepository extends AuthorityRepository<User, UserAuthority> {
}
