package com.rawa.cloud.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OpenApiFile {
    private String keyUnitId;

    private String keyUnitName;

    private String uuid;

    private String name;

    private Date modifiedTime;

    private List<String> tags;
}
