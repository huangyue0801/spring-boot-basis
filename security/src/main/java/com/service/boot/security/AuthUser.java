package com.service.boot.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthUser extends org.springframework.security.core.userdetails.User {
    public User user;

    public AuthUser(User user, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
        this.user = user;
        user.setPassword(null);
    }
}
