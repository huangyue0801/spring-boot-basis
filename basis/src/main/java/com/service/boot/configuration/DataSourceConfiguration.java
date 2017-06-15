package com.service.boot.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.service.boot.configuration.config.JdbcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Resource
    private JdbcConfig jdbcConfig;

    @Bean
    public DataSource dataSource() throws Exception {
        DruidDataSource druidDataSource = (DruidDataSource) DataSourceBuilder.create()
                .driverClassName(jdbcConfig.driverClass)
                .type(DruidDataSource.class)
                .url(jdbcConfig.url)
                .username(jdbcConfig.userName)
                .password(jdbcConfig.password).build();
        druidDataSource.setFilters("stat,wall,log4j");
        LOGGER.info(jdbcConfig.toString());
        return druidDataSource;
    }

}
