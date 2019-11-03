package com.rawa.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "nas")
@Validated
@Data
public class NasProperties {
    @NotNull
    private String path; // 文件存储路径
}
