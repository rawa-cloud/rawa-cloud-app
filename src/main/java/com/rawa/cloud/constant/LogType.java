package com.rawa.cloud.constant;

public enum LogType {

    ADD ("add", "新增"),
    UPDATE ("update", "更新"),
    DELETE ("delete", "删除");



    public final String value;

    public final String desc;

    private LogType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
