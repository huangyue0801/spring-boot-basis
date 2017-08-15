package com.service.boot.security;

import com.service.boot.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserLogoutHandler implements LogoutSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLogoutHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String device = request.getHeader("device");
        LOGGER.info("退出登录 device={}", device);
        if (User.DEVICE_LIST.contains(device)) {
            PrintWriter writer = response.getWriter();
            Map<String, Object> map = new HashMap<>(1);
            map.put("code", 0);
            writer.write(JSON.toJSON(map));
            writer.close();
        } else {
            response.sendRedirect("/loginPage");
        }
    }
}
