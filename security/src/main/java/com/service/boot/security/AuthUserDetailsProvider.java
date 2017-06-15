package com.service.boot.security;

import com.service.boot.utils.validator.Validators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthUserDetailsProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserDetailsProvider.class);

    @Resource
    private AuthUserService service;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordVerificationAuthenticationToken token = (UsernamePasswordVerificationAuthenticationToken) authentication;
        if (!token.isVerificationCode()) {
            LOGGER.error("验证码验证失败！");
            throw new CaptchaAuthenticationException("验证码验证失败");
        }
        String name = authentication.getName();
        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();
        int type;
        if (Validators.isEmail(name)) {
            type = 3;
        } else if (Validators.isMobile(name)) {
            type = 2;
        } else {
            type = 1;
        }
        User user = service.loadByUser(type, name);
        String password = Security.encodePassword((String) credentials);//vbao值是密码盐
        LOGGER.info("用户密码验证！ 用户名(name, principal)=\"({}, {})\" 明文密码\"{}\" 加密密码=\"{}\"", name, principal, credentials, password);
        if (user == null) {
            LOGGER.error("没有此用户 \"{}\" 的信息", name);
            throw new BadCredentialsException("没有此用户");
        }
        if (!password.equals(user.getPassword())) {
            LOGGER.error("用户密码验证失败！");
            throw new BadCredentialsException("密码验证失败");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        Set<User.Role> roleList = service.getUserRoles(user);
        for (User.Role role : roleList) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        user.setPassword(null);
        user.setRoleId(null);
        AuthUser userDetails = new AuthUser(user, name, password, authorities);
        UsernamePasswordVerificationAuthenticationToken newToken = new UsernamePasswordVerificationAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        newToken.setVerificationCode(token.isVerificationCode());
        return newToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordVerificationAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
