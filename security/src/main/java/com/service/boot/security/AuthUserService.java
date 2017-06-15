package com.service.boot.security;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class AuthUserService {

    @Resource
    private AuthUserDao authUserDao;

    public User loadByUser(int type, String username) {
        return authUserDao.loadByUser(type, username);
    }

    public Set<User.Role> getUserRoles(User user) {
        return authUserDao.getUserRoles(user);
    }

}
