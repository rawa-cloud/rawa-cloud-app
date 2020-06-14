package com.rawa.cloud.bean;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Component
public class LicenseHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private Licence licence;

    private long timestamp = 0l;

    private boolean result = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long now = new Date().getTime();
        if (now - timestamp >= 1000 * 60 * 60 * 24) licence.setValidated(false);
        if(!licence.isValidated() || !result) {
            licence.setValidated(true);
            timestamp = new Date().getTime();
            if (!licence.checkMac()) {
                result = false;
                throw new AppException(HttpJsonStatus.LICENSE_INVALID, licence.getText());
            }
            if (!licence.checkExpiredDate()) {
                result = false;
                throw new AppException(HttpJsonStatus.LICENSE_EXPIRED, licence.getExpiredDate());
            }
            result = true;
            return true;
        } else {
            return true;
        }
    }
}
