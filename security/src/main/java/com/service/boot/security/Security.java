package com.service.boot.security;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class Security {

    private static Md5PasswordEncoder encoder = new Md5PasswordEncoder();

    static {
        encoder.setEncodeHashAsBase64(true);
    }

    public static AuthUser getAuthUser() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null) {
            Authentication authentication = sc.getAuthentication();
            if (authentication != null) {
                Object o = authentication.getPrincipal();
                if (o != null && o instanceof AuthUser) {
                    return ((AuthUser) o);
                }
            }
        }
        return null;
    }

    public static User getLoginUser() {
        AuthUser authUser = getAuthUser();
        if (authUser != null) {
            return authUser.user;
        }
        return null;
    }

    public static String encodePassword(String password) {
        return encoder.encodePassword(password, "vbao");
    }

}
