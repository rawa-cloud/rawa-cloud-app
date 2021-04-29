package com.rawa.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppProperties {
    @NotNull
    private String officeHost; // onlyoffice 主机

//    @NotNull
//    private String officeUrl; // office服务地址
//
    @NotNull
    private  String temp; // 文件缓存目录

    @NotNull
    private  String importPath; // 文件导入目录

    @NotNull
    private  String exportPath; // 文件导出目录

    private String syncCron; // 数据同步调度

}
