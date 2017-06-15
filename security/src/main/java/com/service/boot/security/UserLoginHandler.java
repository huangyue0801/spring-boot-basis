package com.service.boot.security;

import com.service.boot.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler implements AuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String device = request.getHeader("device");
        LOGGER.info("登录成功 device={}", device);
        if ("ios".equals(device) || "android".equals(device) || "ajax".equals(device)) {
            AuthUser user = (AuthUser) authentication.getPrincipal();
            responseUserInfo(response, user);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String device = request.getHeader("device");
        String message = "登录失败！";
        if (exception instanceof CaptchaAuthenticationException) {
            message = "验证码错误！";
        }
        if ("ios".equals(device) || "android".equals(device) || "ajax".equals(device)) {
            responseFailure(response, message);
        } else {
            request.getSession().setAttribute("message", message);
            request.getRequestDispatcher("/loginPage").forward(request, response);
        }
    }

    private void responseUserInfo(HttpServletResponse response, AuthUser user) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        user.user.setPassword(null);
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", 0);
        map.put("data", user.user);
        writer.write(JSON.toJSON(map));
        writer.close();
    }

    private void responseFailure(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", -1);
        map.put("message", message);
        writer.write(JSON.toJSON(map));
        writer.close();
    }
}
