package com.rawa.cloud.constant;

public enum HttpJsonStatus {
    SUCCESS("000000", "操作成功"), // 成功
    ACCESS_DENIED("110000", "没有访问权限"),
    AUTH_REQUIRED("100000", "未登录"),
    AUTH_PASSWORD_FAIL("100001", "密码错误"),
    AUTH_IP_FAIL("100002", "IP地址受限"),
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
    FILE_DIR_NOT_FOUND("310007", "文件目录不存在"),
    USER_ADD_DENIED("320001", "无权添加用户"),
    OFFICE_CONVERT_FAIL("330000", "office预览转换失败"),
    SHARE_PASSWORD_ERROR("410000", "提取码错误"),
    LIBRARY_CATALOG_EXISTS("500000", "库模板已存在"),
    LIBRARY_CATALOG_NOT_FOUND("500001", "库模板未找到"),
    LIBRARY_CATALOG_FIELD_NOT_FOUND("500002", "库模板字段未找到"),
    LIBRARY_CATALOG_FIELD_EXISTS("500003", "库模板字段已存在"),
    LIBRARY_CATALOG_FIELD_DICT_EXISTS("500004", "库模板字段字典已存在"),
    LIBRARY_CATALOG_FIELD_DICT_NOT_FOUND("500005", "库模板字段字典未找到"),
    LIBRARY_NO_ENUM_FIELD_TYPE("500006", "不支持枚举字段类型"),
    LIBRARY_EXISTS("500007", "库已存在"),
    LIBRARY_NOT_FOUND("500008", "库未找到"),
    LINK_NOT_FOUND("601000", "链接未找到"),
    FAVORITE_ITEM_EXISTS("602000", "收藏名称已存在"),
    FAVORITE_CATALOG_EXISTS("602001", "收藏类别名称已存在"),
    LICENSE_USER_LIMIT("603001", "超出License 用户限制上限"),
    LICENSE_EXPIRED("603002", "License 已过期"),
    LICENSE_ILLEGAL("603003", "License 格式错误"),
    LICENSE_INVALID("603004", "无效 License"),
    EXPORT_PLAN_EXIST("604001", "已存在导出计划"),
    EXPORT_PLAN_NOT_EXIST_DIR("604002", "导出文件夹不存在"),
    EXPORT_PLAN_NOT_FOUND("604004", "导出计划不存在"),
    EXPORT_PLAN_ONGOING("604005", "导出计划正在执行，请等待执行完毕"),
    IMPORT_PLAN_EXIST("605001", "已存在导入计划"),
    IMPORT_PLAN_NOT_EXIST_DIR("605002", "导入文件夹不存在"),
    IMPORT_PLAN_NOT_FOUND("605004", "导入计划不存在"),
    IMPORT_PLAN_ONGOING("605005", "导入计划正在执行，请等待执行完毕"),
    // TODO ...
    ERROR("200000", "服务器异常"),
    RECORD_NOT_FOUND("200001", "未找到记录"),
    OPT_NOT_ALLOWED("200002", "操作不允许");

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
