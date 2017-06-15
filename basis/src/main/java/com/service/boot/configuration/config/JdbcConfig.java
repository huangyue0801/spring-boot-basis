package com.service.boot.configuration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = "jdbc.properties", encoding = "utf-8")
@Component
public final class JdbcConfig {

    /**
     * 环境
     */
    @Value("${${spring.profiles.active}.datasource.active}")
    public String active;

    /**
     * 驱动名称
     */
    @Value("${${spring.profiles.active}.datasource.driver-class-name}")
    public String driverClass;

    /**
     * 数据库连接url
     */
    @Value("${${spring.profiles.active}.datasource.url}")
    public String url;

    /**
     * 数据库用户名
     */
    @Value("${${spring.profiles.active}.datasource.username}")
    public String userName;

    /**
     * 数据库密码
     */
    @Value("${${spring.profiles.active}.datasource.password}")
    public String password;

    @Override
    public String toString() {
        return String.format("\nJDBC配置 \n环境=\"%s\" \n驱动=\"%s\" \n地址=\"%s\" \n账号=\"%s\" \n密码=\"%s\"",
                active, driverClass, url, userName, password);
    }
}
