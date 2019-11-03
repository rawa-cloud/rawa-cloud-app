package com.rawa.cloud.constant;

public enum Umask {
    ACCESS(1L, "访问文件权限"), // 访问文件
    MK_DIR(1L << 1, "新建文件夹权限"), // 新建文件夹
    NEW_FILE(1L << 2, "新建文件权限"), // 新建文件
    UPDATE_FILE(1L << 3, "更新文件权限"), // 更新文件
    RENAME(1L << 4, "访问文件权限"), // 重命名文件
    RECYCLE(1L << 5, "重命名文件权限"), // 回收文件
    DELETE(1L << 6, "删除文件权限"), // 删除文件
    RECOVER(1L << 7, "恢复文件权限"), // 恢复文件
    PREVIW(1L << 8, "预览文件权限"),  // 预览文件
    DOWNLOAD(1L << 9, "下载文件权限"), // 下载文件
    LINK(1L << 10, "生成外链权限"), // 生成外链
    RECORD_DOWNLOAD(1L << 11, "文件历史下载权限"), // 文件历史下载
    RECORD_ROLLBACK(1L << 12, "文件历史回滚权限"); // 文件历史回滚

    public final Long value;

    public final String desc;

    private Umask(Long value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static Boolean hasAny(Long umask, Umask ...bits) {
        for(Umask bit: bits){
            if ((bit.value & umask) == bit.value) return true;
        }
        return false;
    }

    public static Boolean hasAll(Long umask, Umask ...bits) {
        for(Umask bit: bits){
            if ((bit.value & umask) != bit.value) return false;
        }
        return true;
    }
}
