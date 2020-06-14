package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.Property;
import com.rawa.cloud.repository.PropertyRepository;
import com.rawa.cloud.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyRepository propertyRepository;

    @Override
    public String getValue(String key) {
        Property p = propertyRepository.findByName(key);
        return p == null ? null : p.getValue();
    }

    @Override
    public void add(String key, String value) {
        Property p = propertyRepository.findByName(key);
        if (p == null) {
            p = new Property();
            p.setName(key);
        }
        p.setValue(value);
        propertyRepository.save(p);
    }

    @Override
    public void add(String key, String value, Boolean config) {
        Property p = propertyRepository.findByName(key);
        if (p == null) {
            p = new Property();
            p.setName(key);
        }
        p.setValue(value);
        p.setConfig(config);
        propertyRepository.save(p);
    }

    @Override
    public List<Property> query() {
        return propertyRepository.findAllByConfigIsTrue();
    }
}
