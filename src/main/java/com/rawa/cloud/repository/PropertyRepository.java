package com.rawa.cloud.repository;

import com.rawa.cloud.domain.Property;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface PropertyRepository extends BaseRepository<Property> {

    Property findByName(String name);

    List<Property> findAllByConfigIsTrue();

}
