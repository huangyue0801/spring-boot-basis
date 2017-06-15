package com.service.boot.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.service.boot.configuration.handler.ExceptionHandler;
import com.service.boot.configuration.interceptor.RequestInterceptor;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.servlet.MultipartConfigElement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfiguration.class);

    /**
     * 注册请求拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LOGGER.info("注册请求拦截器");
        // 请求拦截器
        registry.addInterceptor(new RequestInterceptor());
    }

    /**
     * 添加异常处理器
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        LOGGER.info("添加异常处理器");
        // 添加异常处理器
        exceptionResolvers.add(new ExceptionHandler());
    }

    /**
     * 添加消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        LOGGER.info("添加消息转换器");
        converters.add(getJacksonMessageConverter());
    }

    /**
     * 注册视图解析器
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        LOGGER.info("注册视图解析器");
        // 模板解析
        registry.viewResolver(getBeetlSpringViewResolver());
        // JSP视图解析器
        registry.jsp("/WEB-INF/pages/", ".jsp");
    }

    /**
     * 静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LOGGER.info("静态资源处理");
        registry.addResourceHandler("/**").addResourceLocations("/");
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        LOGGER.info("文件上传配置");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制，超出会抛异常！
        factory.setMaxFileSize("1024MB"); //KB,MB
        // 设置上传数据总大小
        factory.setMaxRequestSize("1024MB");
        // 设置文件存放位置
//        factory.setLocation("/Users/sanders/Documents/workspace/spring_boot_template/");
        return factory.createMultipartConfig();
    }

    /**
     * 替换错误资源显示
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        LOGGER.info("替换错误资源显示");
        return (container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            container.addErrorPages(error401Page, error404Page, error500Page);
        });
    }

    /**
     * 构造并返回Beetl模板视图解析器
     */
    public BeetlSpringViewResolver getBeetlSpringViewResolver() {
        LOGGER.info("构造并返回Beetl模板视图解析器");
        try {
            BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
            ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader("/templates/");
            beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);
            beetlGroupUtilConfiguration.init();
            BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
//            beetlSpringViewResolver.setPrefix("/templates/");
            beetlSpringViewResolver.setSuffix(".html");
            beetlSpringViewResolver.setContentType("text/html;charset=utf-8");
            beetlSpringViewResolver.setOrder(0);
            beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
            return beetlSpringViewResolver;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造并返回Jackson消息转换器
     */
    public MappingJackson2HttpMessageConverter getJacksonMessageConverter() {
        LOGGER.info("构造并返回Jackson消息转换器");
        ObjectMapper om = Jackson2ObjectMapperBuilder.json().build();
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(om);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(mediaTypes);
        return converter;
    }
}
