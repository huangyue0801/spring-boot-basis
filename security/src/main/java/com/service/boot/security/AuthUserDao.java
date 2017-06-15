package com.service.boot.security;

import com.service.boot.basis.dao.GenericDao;
import com.service.boot.json.JSON;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class AuthUserDao extends GenericDao {

    public User loadByUser(int type, String username) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE ");
        switch (type) {
            case 1://username
                sql.append("username=?");
                break;
            case 2://phone
                sql.append("phone=?");
                break;
            case 3://email
                sql.append("email=?");
                break;
            default:
                return null;

        }
        return super.get(sql.toString(), User.class, username);
    }

    private void appendChild(Set<User.Role> roles, String child) {
        if (child != null) {
            try {
                int[] childs = JSON.parseObject(child, int[].class);
                if (childs != null) {
                    for (Integer id : childs) {
                        User.Role role = super.get("SELECT * FROM role WHERE id=?", User.Role.class, id);
                        if (role != null) {
                            roles.add(role);
                            appendChild(roles, role.getChild());
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public Set<User.Role> getUserRoles(User user) {
        Set<User.Role> roles = new HashSet<>();
        Integer roleId = user.getRoleId();
        if (roleId != null) {
            User.Role role = super.get("SELECT * FROM role WHERE id=?", User.Role.class, roleId);
            if (role != null) {
                user.setRole(role);
                roles.add(role);
                appendChild(roles, role.getChild());
                role.setChild(null);
            }
        }
        return roles;
    }
}
