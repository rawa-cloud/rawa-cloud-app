package com.rawa.cloud.helper;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;

public class ExceptionHelper {
    public static AppException throwAppException (HttpJsonStatus status, Object data){
        return new AppException(status, data);
    }
}
