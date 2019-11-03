package com.rawa.cloud.constant;

public enum HttpJsonStatus {
    SUCCESS("000000", "操作成功"), // 成功
    ACCESS_DENIED("110000", "没有访问权限"),
    AUTH_REQUIRED("100000", "未登录"),
    AUTH_PASSWORD_FAIL("100001", "密码错误"),
    VALID_FAIL("300000", "参数校验失败"),
    MODEL_CONVERT_ERROR("300001", "模型转换失败"),
    FILE_NOT_FOUND("300002", "未找到文件"),
    USER_NOT_FOUND("300003", "用户未找到"),
    USER_EXISTS("300004", "用户已存在"),
    FILE_RECORD_NOT_FOUND("300005", "文件记录未找到"),
    AUTHORITY_NOT_FOUND("300006", "权限未找到"),
    DEPT_NOT_FOUND("301000", "部门未找到"),
    DEPT_EXISTS("301001", "部门已存在"),
    FILE_OPT_DENIED("310001", "无文件操作权限"),
    FILE_EXIST("310002", "文件已存在"),
    RAW_FILE_LOST("310003", "物理文件丢失"),
    FILE_CAPACITY_OVERFLOW("310004", "文件夹空间已满"),
    FILE_ILLEGAL_SUFFIX("310005", "不合法文件后缀"),
    FILE_DELETED("310006", "文件已被删除"),
    USER_ADD_DENIED("320001", "无权添加用户"),
    OFFICE_CONVERT_FAIL("330000", "office预览转换失败"),
    SHARE_PASSWORD_EEROR("410000", "提取码错误"),
    // TODO ...
    ERROR("200000", "服务器异常"),
    RECORD_NOT_FOUND("200001", "未找到记录");

    private final String code;
    private final String message;

    private HttpJsonStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
