package com.service.boot.security;

import org.springframework.security.core.AuthenticationException;

public class CaptchaAuthenticationException extends AuthenticationException {
    public CaptchaAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public CaptchaAuthenticationException(String msg) {
        super(msg);
    }
}
