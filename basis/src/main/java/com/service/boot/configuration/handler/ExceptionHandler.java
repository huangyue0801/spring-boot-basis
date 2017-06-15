package com.service.boot.configuration.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Controller
public class ExceptionHandler implements HandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //forward 转发
        //redirect 重定向
        LOGGER.error("异常", ex);
        String device = request.getHeader("device");
        Pair<Integer, String> pair = analysis(ex);
        if ("ios".equals(device) || "android".equals(device) || "ajax".equals(device)) {
            return new ModelAndView(String.format("redirect:/api/fail?code=%d&message=%s", pair.getFirst(), URLEncoder.encode(pair.getSecond())));
        } else {
            ModelAndView mav = new ModelAndView("fail");
            mav.addObject("code", pair.getFirst());
            mav.addObject("message", pair.getSecond());
            return mav;
        }
    }

    @ResponseBody
    @GetMapping("/api/fail")
    public Map<Object, Object> apiException(int code, String message) {
        Map<Object, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        return map;
    }

    /**
     * 处理各种异常
     *
     * @param ex
     * @return
     */
    private Pair<Integer, String> analysis(Exception ex) {
        int code = -1;
        String message = "";
        String e = ex.getClass().getName();
        if ("org.springframework.security.core.userdetails.UsernameNotFoundException".equals(e)) {
            message = "没有此用户";
        } else if ("org.springframework.security.access.AccessDeniedException".equals(e)) {
            message = "权限不足";
        } else if ("org.springframework.web.HttpRequestMethodNotSupportedException".equals(e)) {
            message = "未知错误";
        } else if ("com.service.boot.security.CaptchaAuthenticationException".equals(e)) {
            message = ex.getMessage();
        }
        return Pair.of(code, message);
    }
}
