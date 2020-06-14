package com.rawa.cloud.runner.data;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class FileDataGenerator implements DataGenerator{

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired

    @Override
    public void generate() {
        List<File> files = fileRepository.findAllByParentIsNull();
        File root = files.size() < 1 ? null : files.get(0);
        if (root != null && "/".equals(root.getName())) return;
        fileRepository.deleteAll(files);
        root = new File();
        root.setStatus(true);
        root.setName("/");
        root.setDir(true);
        root.setCreationBy("root");
        root.setLastChangeBy("root");
        root.setLastChangeTime(new Date());
        root.setCreationTime(new Date());
        root.setSystem(true);

        fileRepository.save(root);

        File userRoot = new File();
        userRoot.setStatus(true);
        userRoot.setName("Users");
        userRoot.setDir(true);
        userRoot.setCreationBy("root");
        userRoot.setLastChangeBy("root");
        userRoot.setLastChangeTime(new Date());
        userRoot.setCreationTime(new Date());
        userRoot.setSystem(true);
        userRoot.setParent(root);

        fileRepository.save(userRoot);

        File rootUserFile = new File();
        rootUserFile.setName("root");
        rootUserFile.setStatus(true);
        rootUserFile.setDir(true);
        rootUserFile.setCreationBy("root");
        rootUserFile.setLastChangeBy("root");
        rootUserFile.setLastChangeTime(new Date());
        rootUserFile.setCreationTime(new Date());
        rootUserFile.setParent(userRoot);
        rootUserFile.setSystem(true);
        rootUserFile.setUser(userRepository.findUserByUsername("root"));
        fileRepository.save(rootUserFile);
    }
}
