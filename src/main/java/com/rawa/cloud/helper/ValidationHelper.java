package com.rawa.cloud.helper;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import org.springframework.validation.BindingResult;

public class ValidationHelper {
    public static void validate (BindingResult result) throws AppException {
        if(result.hasErrors()){
            throw ExceptionHelper.throwAppException(HttpJsonStatus.VALID_FAIL, result.getAllErrors());
        }
    }
}
