package com.rawa.cloud.runner.data;

import com.rawa.cloud.properties.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataRunner implements ApplicationRunner {
    @Autowired
    RoleDataGenerator roleDataGenerator;

    @Autowired
    UserDataGenerator userDataGenerator;

    @Autowired
    DeptDataGenerator deptDataGenerator;

    @Autowired
    FileDataGenerator fileDataGenerator;

    @Autowired
    PropertyDataGenerator propertyDataGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleDataGenerator.generate();
        userDataGenerator.generate();
        deptDataGenerator.generate();
        fileDataGenerator.generate();
        propertyDataGenerator.generate();
    }
}
