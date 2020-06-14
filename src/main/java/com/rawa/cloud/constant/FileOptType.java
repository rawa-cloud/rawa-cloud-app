package com.rawa.cloud.constant;

public enum FileOptType {

     upload ("上传文件"),
     mkdir ("新建文件夹"),
     download (" 下载文件"),
     update (" 更新文件"),
     rename (" 重命名文件"),
     preview (" 预览文件"),
     collect (" 收藏文件"),
     share (" 共享文件");

    public final String desc;

    private FileOptType(String desc) {
        this.desc = desc;
    }
}
