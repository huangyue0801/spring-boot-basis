package com.service.boot.configuration;

import com.service.boot.configuration.config.RedisConfig;
import com.service.boot.json.JSON;
import com.service.boot.utils.io.IOClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis配置
 */
@Configuration
@EnableCaching
public class RedisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    @Resource
    private RedisConfig redisConfig;

    public static JedisConnectionFactory getJedisConnectionFactory(RedisConfig redisConfig){
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(redisConfig.host);
        redisConnectionFactory.setPort(redisConfig.port);
        redisConnectionFactory.setDatabase(redisConfig.index);
        redisConnectionFactory.setPassword(redisConfig.password);
        redisConnectionFactory.setUsePool(false);
        JedisPoolConfig config = new JedisPoolConfig();
        //设置最大实例总数
        config.setMaxTotal(150);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(30);
        config.setMinIdle(10);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(3 * 1000);
        // 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        // 在还会给pool时，是否提前进行validate操作
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(500);
        config.setSoftMinEvictableIdleTimeMillis(1000);
        config.setTimeBetweenEvictionRunsMillis(1000);
        config.setNumTestsPerEvictionRun(100);
        redisConnectionFactory.setPoolConfig(config);
        return redisConnectionFactory;
    }

    @Bean
    @Qualifier("jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        return getJedisConnectionFactory(redisConfig);
    }

    @Bean
    @Qualifier("cacheRedisTemplate")
    public RedisTemplate cacheRedisTemplate(@Qualifier("jedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate template = new RedisTemplate();
        LOGGER.info("\n缓存Redis{}", redisConfig.toString());
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

    @Bean
    @Qualifier("cacheManager")
    public CacheManager cacheManager(@Qualifier("cacheRedisTemplate") RedisTemplate cacheRedisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(cacheRedisTemplate);
//        redisCacheManager.setDefaultExpiration(redisConfig.expire);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(RedisConfiguration.class.getResourceAsStream("/redis-cache.json")));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim().replace(" ", ""));
            }
            RedisCache redisCache = JSON.parseBean(sb.toString(), RedisCache.class);
            if (redisCache.cacheNames != null) {
                List<String> cacheNames = new ArrayList<>();
                Map<String, Long> expiresMap = new HashMap<>();
                for (RedisCache.CacheName name : redisCache.cacheNames) {
                    if (name.name != null && !"".equals(name.name.trim())) {
                        cacheNames.add(name.name);
                    }
                    if (name.expires > 0) {
                        expiresMap.put(name.name, name.expires);
                    }
                    LOGGER.info("redis缓存 名称cacheName=\"{}\" 过期expires=\"{}\"  单位=\"{}\"", name.name, name.expires, name.expires_unit);
                }
                redisCacheManager.setCacheNames(cacheNames);
                redisCacheManager.setExpires(expiresMap);
            }
        } catch (Exception e) {
            LOGGER.error("读取redis-cache.json配置文件失败", e);
        } finally {
            IOClose.close(reader);
        }
        return redisCacheManager;
    }

    private static final class RedisCache {

        public List<CacheName> cacheNames;

        private static final class CacheName {
            public String name;
            public long expires;
            public String expires_unit;
        }

    }
}
