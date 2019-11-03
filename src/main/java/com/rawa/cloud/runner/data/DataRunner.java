package com.rawa.cloud.runner.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataRunner implements ApplicationRunner {


    @Autowired
    RoleDataGenerator roleDataGenerator;

    @Autowired
    UserDataGenerator userDataGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleDataGenerator.generate();
        userDataGenerator.generate();
    }
}
