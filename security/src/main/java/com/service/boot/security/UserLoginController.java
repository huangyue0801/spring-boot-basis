package com.service.boot.security;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserLoginController {

    @Resource
    private Producer producer;

    @GetMapping("/loginPage")
    public ModelAndView loginPage(HttpServletRequest request) {
        String device = request.getHeader("device");
        HttpSession session = request.getSession();
        Object message = session.getAttribute("message");
        if ("ios".equals(device) || "android".equals(device) || "ajax".equals(device)) {
            return new ModelAndView("forward:/loginApi");
        }
        ModelAndView mav = new ModelAndView("login");
        if (message != null) {
            session.removeAttribute("message");
            mav.addObject("message", message);
        }
        return mav;
    }

    @GetMapping("/loginApi")
    @ResponseBody
    public Map<String, Object> loginApi(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", -1);
        map.put("message", "请登录");
        return map;
    }

    @GetMapping("/captcha")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = producer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = producer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

}
