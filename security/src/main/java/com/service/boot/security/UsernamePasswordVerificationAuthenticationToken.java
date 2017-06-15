package com.service.boot.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by sanders on 2017/3/19.
 */
public class UsernamePasswordVerificationAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private boolean verificationCode;

    public UsernamePasswordVerificationAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UsernamePasswordVerificationAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public boolean isVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(boolean verificationCode) {
        this.verificationCode = verificationCode;
    }
}
