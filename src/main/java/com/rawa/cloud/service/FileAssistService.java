package com.rawa.cloud.service;

import com.rawa.cloud.model.fileassist.FileAssistAutoSaveRequestModel;

import java.io.File;

public interface FileAssistService {
    File download (Long id);

    void autoSave (Long id, FileAssistAutoSaveRequestModel model);
}
