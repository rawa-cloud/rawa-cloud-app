package com.rawa.cloud.service;

import com.rawa.cloud.dto.OpenApiFile;
import com.rawa.cloud.model.openapi.OpenApiFileParams;
import org.springframework.data.domain.Page;

public interface OpenApiService {
    Page<OpenApiFile> queryFiles (OpenApiFileParams params);
}
