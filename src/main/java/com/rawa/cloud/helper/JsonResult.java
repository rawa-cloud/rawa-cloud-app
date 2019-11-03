package com.rawa.cloud.helper;

import com.rawa.cloud.constant.HttpJsonStatus;
import lombok.Data;

@Data
public class JsonResult<E> {

    private E data;

    private String code;

    private String message;

    public static final <T> JsonResult<T> success (T data) {
        return new JsonResult<>(data, HttpJsonStatus.SUCCESS.getCode(), HttpJsonStatus.SUCCESS.getMessage());
    }

    public static final <T> JsonResult<T> fail (HttpJsonStatus status, T data) {
        return new JsonResult<>(data, status.getCode(),status.getMessage());
    }

    public JsonResult(E data, String code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }
}
