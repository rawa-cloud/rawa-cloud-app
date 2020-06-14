package com.rawa.cloud.exception;

import com.rawa.cloud.constant.HttpJsonStatus;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class AppException extends RuntimeException{

    public static Supplier<RuntimeException> optionalThrow(HttpJsonStatus status, Object data) {
        return () -> new AppException(status, data);
    }

    private HttpJsonStatus status;

    private Object data;

    private String message;

    public AppException() {
    }

    public AppException(HttpJsonStatus status, Object data) {
        this.status = status;
        this.data = data;
        this.message = status.getMessage();
    }

    public AppException(HttpJsonStatus status, String message, Object data) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
}
