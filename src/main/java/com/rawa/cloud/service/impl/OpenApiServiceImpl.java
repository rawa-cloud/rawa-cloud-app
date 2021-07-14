package com.rawa.cloud.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Unit;
import com.rawa.cloud.dto.OpenApiFile;
import com.rawa.cloud.model.openapi.OpenApiFileParams;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.UnitRepository;
import com.rawa.cloud.service.OpenApiService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenApiServiceImpl implements OpenApiService {
    @Resource
    FileRepository fileRepository;

    @Resource
    UnitRepository unitRepository;

    @Override
    public Page<OpenApiFile> queryFiles(OpenApiFileParams params) {
        Page<File> filePage = fileRepository.findAll((root, query, cb) ->
                        cb.and(
                                cb.equal(root.get("dir"), false),
                                cb.equal(root.get("status"), true),
                                StringUtils.isEmpty(params.getKeyUnitId()) ? cb.isTrue(cb.literal(Boolean.TRUE)) :
                                        cb.equal(root.get("keyUnit"), params.getKeyUnitId()),
                                params.getModifiedTimeStart() == null ? cb.isTrue(cb.literal(Boolean.TRUE)) :
                                        cb.greaterThanOrEqualTo(root.get("lastChangeTime"), params.getModifiedTimeStart()),
                                params.getModifiedTimeEnd() == null ? cb.isTrue(cb.literal(Boolean.TRUE)) :
                                        cb.lessThan(root.get("lastChangeTime"), params.getModifiedTimeEnd())
                        ),
                params.toPage());
        Map<String, Unit> unitMap = filePage.getContent().size() < 1 ? new HashMap<>() :
                    unitRepository.findAllByCodeIn(
                            filePage.getContent().stream()
                            .map(s -> s.getKeyUnit())
                            .filter(s -> s != null)
                            .collect(Collectors.toList())
                    ).stream().collect(Collectors.toMap(s -> s.getCode(), s -> s));
        return filePage.map(s -> {
            OpenApiFile f = new OpenApiFile();
            f.setKeyUnitName(Optional.ofNullable(unitMap.get(s.getKeyUnit())).map(w -> w.getName()).orElse(null));
            f.setKeyUnitId(s.getKeyUnit());
            f.setModifiedTime(s.getLastChangeTime());
            f.setName(s.getName());
            f.setUuid(s.getUuid());
            if (!StringUtils.isEmpty(s.getTags())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<String> tags = objectMapper.readValue(s.getTags(), ArrayList.class);
                    f.setTags(tags);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return f;
        });
    }
}
