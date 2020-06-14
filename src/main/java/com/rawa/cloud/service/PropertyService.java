package com.rawa.cloud.service;

import com.rawa.cloud.domain.Property;

import java.util.List;

public interface PropertyService {

    String getValue (String key);

    void add (String key, String value);

    void add (String key, String value, Boolean config);

    List<Property> query ();
}