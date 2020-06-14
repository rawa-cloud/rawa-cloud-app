package com.rawa.cloud.repository;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.repository.common.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FileRepository extends BaseRepository<File>, JpaSpecificationExecutor<File> {

    List<File> findAllByParent(File parent);

    File findFileByParentIsNull();

    List<File> findAllByParentIsNull();

    List<File> findAllByIdIn(List<Long> ids);

    File findByParentAndName (File Parent, String name);

    List<File> findAllByUser(User user);

    int countAllByStatusIsTrue();
}
