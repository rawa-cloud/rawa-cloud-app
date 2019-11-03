package com.rawa.cloud.repository;

import com.rawa.cloud.domain.File;
import com.rawa.cloud.repository.common.BaseRepository;

import java.util.List;

public interface FileRepository extends BaseRepository<File> {
    // 查找有效私有根目录
    File findFileByParentIsNullAndPersonalIsTrueAndStatusIsTrueAndCreationBy(String username);

    //查找有效公共根目录列表
    List<File> findFilesByParentIsNullAndPersonalIsFalseAndStatusIsTrue();

    List<File> findAllByIdIn(List<Long> ids);

    List<File> findAllByParent(File parent);
}
