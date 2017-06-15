package com.service.boot.configuration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = "redis.properties")
@Component
public class RedisConfig {

    @Value("${${spring.profiles.active}.redis.active}")
    public String active;

    @Value("${${spring.profiles.active}.redis.host}")
    public String host;

    @Value("${${spring.profiles.active}.redis.port}")
    public int port;

    @Value("${${spring.profiles.active}.redis.password}")
    public String password;

    @Value("${${spring.profiles.active}.redis.index}")
    public int index;

    @Override
    public String toString() {
        return String.format("\nRedis配置 \n环境=\"%s\" \n地址=\"%s\" \n端口=\"%d\" \n区块=\"%d\" \n密码=\"%s\"",
                active, host, port, index, password);
    }

}
