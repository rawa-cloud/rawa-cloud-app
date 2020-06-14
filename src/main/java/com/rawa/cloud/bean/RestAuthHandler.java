package com.rawa.cloud.bean;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler, AccessDeniedHandler, AuthenticationEntryPoint {
    @Autowired
    LogService logService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        this.send(res, HttpJsonStatus.ERROR.getCode(), e.getMessage());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        logService.add(Log.build(LogModule.AUTH, LogType.ADD).st("登录").lc(authentication.getName()).end());
        this.send(res, HttpJsonStatus.SUCCESS.getCode(), HttpJsonStatus.SUCCESS.getMessage());
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        logService.add(Log.build(LogModule.AUTH, LogType.DELETE).st("登出").lc(authentication.getName()).end(), authentication.getName());
        this.send(res, HttpJsonStatus.SUCCESS.getCode(), HttpJsonStatus.SUCCESS.getMessage());
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        this.send(res, HttpJsonStatus.AUTH_REQUIRED.getCode(), e.getMessage());
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse res, AccessDeniedException e) throws IOException, ServletException {
        this.send(res, HttpJsonStatus.ACCESS_DENIED.getCode(), e.getMessage());
    }

    private void send (HttpServletResponse res, String code, String message) throws IOException {
        res.setHeader("Content-Type", "application/json;charset=utf-8");
        res.getWriter().print("{\"code\":\"" + code + "\",\"message\":\""+ message +"\"}");
        res.getWriter().flush();
    }

}
