package com.service.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@ServletComponentScan({"com.service.boot"})//扫描servlet
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(value = {"com.service.boot"})
public class BootStartApplication extends SpringBootServletInitializer {

    public static void run(String[] args) {
        SpringApplication application = new SpringApplication(BootStartApplication.class);
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.profiles.active", "develop");
        application.setDefaultProperties(properties);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BootStartApplication.class);
    }
}

