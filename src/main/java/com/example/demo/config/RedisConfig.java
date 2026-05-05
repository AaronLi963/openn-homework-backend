package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

@Configuration
public class RedisConfig {

    private RedisProperties redisProperties;

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @PostConstruct
    public void logConfig() {
        logger.info("Redis host: {}, port: {}", redisProperties.getHost(), redisProperties.getPort());
        
        logger.info("Redis lettuce pool max active: {}, max wait: {}, max idle: {}, min idle: {}",
            redisProperties.getLettuce().getPool().getMaxActive(),
            redisProperties.getLettuce().getPool().getMaxWait(),
            redisProperties.getLettuce().getPool().getMaxIdle(),
            redisProperties.getLettuce().getPool().getMinIdle()
        );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        logger.info("Initialize redis template");
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}   
