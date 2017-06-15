package com.service.boot.configuration;

import com.service.boot.configuration.config.RedisConfig;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfiguration extends SpringHttpSessionConfiguration
        implements EmbeddedValueResolverAware, ImportAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionConfiguration.class);

    @Resource
    private RedisConfig redisConfig;

    @Bean
    @Qualifier("sessionJedisConnectionFactory")
    @Primary
    public JedisConnectionFactory sessionJedisConnectionFactory() {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.active = this.redisConfig.active;
        redisConfig.host = this.redisConfig.host;
        redisConfig.port = this.redisConfig.port;
        redisConfig.password = this.redisConfig.password;
        redisConfig.index = this.redisConfig.index + 1;
        LOGGER.info("\n登录用户Session存储Redis{}", redisConfig.toString());
        return RedisConfiguration.getJedisConnectionFactory(redisConfig);
    }


    private Integer maxInactiveIntervalInSeconds = 1800;

    private ConfigureRedisAction configureRedisAction = new ConfigureNotifyKeyspaceEventsAction();

    private String redisNamespace = "";

    private RedisFlushMode redisFlushMode = RedisFlushMode.ON_SAVE;

    private RedisSerializer<Object> defaultRedisSerializer;

    private Executor redisTaskExecutor;

    private Executor redisSubscriptionExecutor;

    private StringValueResolver embeddedValueResolver;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("sessionJedisConnectionFactory") RedisConnectionFactory connectionFactory,
            RedisOperationsSessionRepository messageListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        if (this.redisTaskExecutor != null) {
            container.setTaskExecutor(this.redisTaskExecutor);
        }
        if (this.redisSubscriptionExecutor != null) {
            container.setSubscriptionExecutor(this.redisSubscriptionExecutor);
        }
        container.addMessageListener(messageListener,
                Arrays.asList(new PatternTopic("__keyevent@*:del"),
                        new PatternTopic("__keyevent@*:expired")));
        container.addMessageListener(messageListener, Arrays.asList(new PatternTopic(
                messageListener.getSessionCreatedChannelPrefix() + "*")));
        return container;
    }

    @Bean
    @Qualifier("sessionRedisTemplate")
    public RedisTemplate<Object, Object> sessionRedisTemplate(
            @Qualifier("sessionJedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            template.setDefaultSerializer(this.defaultRedisSerializer);
        }
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisOperationsSessionRepository sessionRepository(
            @Qualifier("sessionRedisTemplate") RedisOperations<Object, Object> sessionRedisTemplate,
            ApplicationEventPublisher applicationEventPublisher) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(
                sessionRedisTemplate);
        sessionRepository.setApplicationEventPublisher(applicationEventPublisher);
        sessionRepository
                .setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        if (this.defaultRedisSerializer != null) {
            sessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }

        String redisNamespace = getRedisNamespace();
        if (StringUtils.hasText(redisNamespace)) {
            sessionRepository.setRedisKeyNamespace(redisNamespace);
        }

        sessionRepository.setRedisFlushMode(this.redisFlushMode);
        return sessionRepository;
    }

    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public void setRedisNamespace(String namespace) {
        this.redisNamespace = namespace;
    }

    public void setRedisFlushMode(RedisFlushMode redisFlushMode) {
        Assert.notNull(redisFlushMode, "redisFlushMode cannot be null");
        this.redisFlushMode = redisFlushMode;
    }

    private String getRedisNamespace() {
        if (StringUtils.hasText(this.redisNamespace)) {
            return this.redisNamespace;
        }
        return System.getProperty("spring.session.redis.namespace", "");
    }

    public void setImportMetadata(AnnotationMetadata importMetadata) {

        Map<String, Object> enableAttrMap = importMetadata
                .getAnnotationAttributes(EnableRedisHttpSession.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.maxInactiveIntervalInSeconds = enableAttrs
                .getNumber("maxInactiveIntervalInSeconds");
        String redisNamespaceValue = enableAttrs.getString("redisNamespace");
        if (StringUtils.hasText(redisNamespaceValue)) {
            this.redisNamespace = this.embeddedValueResolver.resolveStringValue(redisNamespaceValue);
        }
        this.redisFlushMode = enableAttrs.getEnum("redisFlushMode");
    }

    @Bean
    public InitializingBean enableRedisKeyspaceNotificationsInitializer(
            @Qualifier("sessionJedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        return new EnableRedisKeyspaceNotificationsInitializer(connectionFactory,
                this.configureRedisAction);
    }

    @Autowired(required = false)
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction) {
        this.configureRedisAction = configureRedisAction;
    }

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(
            RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisTaskExecutor")
    public void setRedisTaskExecutor(Executor redisTaskExecutor) {
        this.redisTaskExecutor = redisTaskExecutor;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisSubscriptionExecutor")
    public void setRedisSubscriptionExecutor(Executor redisSubscriptionExecutor) {
        this.redisSubscriptionExecutor = redisSubscriptionExecutor;
    }

    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {
        private final RedisConnectionFactory connectionFactory;

        private ConfigureRedisAction configure;

        EnableRedisKeyspaceNotificationsInitializer(
                RedisConnectionFactory connectionFactory,
                ConfigureRedisAction configure) {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        public void afterPropertiesSet() throws Exception {
            if (this.configure == ConfigureRedisAction.NO_OP) {
                return;
            }
            RedisConnection connection = this.connectionFactory.getConnection();
            try {
                this.configure.configure(connection);
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    LogFactory.getLog(getClass()).error("Error closing RedisConnection", e);
                }
            }
        }
    }

}
