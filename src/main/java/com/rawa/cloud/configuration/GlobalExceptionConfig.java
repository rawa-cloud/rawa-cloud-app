package com.rawa.cloud.configuration;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionConfig {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonResult<Object> exceptionHandler(HttpServletRequest request, Exception exception) throws Exception {
        if (exception instanceof AppException) {
            log.error(((AppException) exception).getStatus().getCode(), ((AppException) exception).getData());
            return JsonResult.fail(((AppException) exception).getStatus(), ((AppException) exception).getMessage(), ((AppException) exception).getData());
        }
        log.error(exception.getMessage(), exception);
        if (exception instanceof AccessDeniedException) {
            return JsonResult.fail(HttpJsonStatus.ACCESS_DENIED, exception.getMessage());
        }
        return JsonResult.fail(HttpJsonStatus.ERROR, exception.getMessage());
    }
}
