package com.service.boot.security;

import com.service.boot.basis.dao.annotation.Column;
import com.service.boot.basis.dao.annotation.Table;

import java.util.Date;

@Table(name = "user")
public class User implements java.io.Serializable{

    @Column(name = "id", primaryKey = true)
    private java.lang.Integer id;
    @Column(name = "name")
    private java.lang.String name;
    @Column(name = "username")
    private java.lang.String username;
    @Column(name = "password")
    private java.lang.String password;
    @Column(name = "phone")
    private java.lang.String phone;
    @Column(name = "email")
    private java.lang.String email;
    @Column(name = "weixin")
    private java.lang.String weixin;
    @Column(name = "role_id")
    private java.lang.Integer roleId;
    @Column(name = "nick_name")
    private java.lang.String nickName;
    @Column(name = "status", defaultValue = "0")
    private Integer status;
    @Column(name = "create_time")
    private java.util.Date createTime;

    private Role role;

    @Table(name = "role")
    public static class Role implements java.io.Serializable{

        @Column(name = "id", primaryKey = true)
        private Integer id;
        @Column(name = "role")
        private String role;
        @Column(name = "name")
        private String name;
        @Column(name = "description")
        private String description;
        @Column(name = "child")
        private String child;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getChild() {
            return child;
        }

        public void setChild(String child) {
            this.child = child;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj != null && obj instanceof Role){
                return this.role.equals(((Role) obj).role);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return role.hashCode();
        }
    }

    @Table(name = "role_path")
    public static class RolePath implements java.io.Serializable {

        @Column(name = "id", primaryKey = true)
        private Integer id;
        @Column(name = "role_id")
        private Integer roleId;
        @Column(name = "name")
        private String name;
        @Column(name = "path")
        private String path;
        @Column(name = "description")
        private String description;
        @Column(name = "create_time")
        private java.util.Date createTime;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
