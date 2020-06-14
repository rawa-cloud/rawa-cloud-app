package com.rawa.cloud.constant;

public enum LogModule {

    DEPT ("dept", "部门"),
    USER ("user", "用户"),
    FILE ("file", "文件"),
    RECYCLE ("recycle", "回收站"),
    AUTHORITY ("authority", "权限"),
    SHARE ("share", "分享"),
    LIB_CONFIG ("lib_config", "库模板"),
    LIB ("LIB", "自定义库"),
    AUTH ("auth", "认证");



    public final String value;

    public final String desc;

    private LogModule(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
