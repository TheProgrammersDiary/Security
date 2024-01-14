package com.evalvis.security;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.embedded.RedisServer;

@Configuration
@ComponentScan("com.evalvis.security")
public class TestRedisConfig {
    @Value("${spring.data.redis.port}") String port;
    private RedisServer redisServer;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(
                "localhost", Integer.parseInt(port)
        );
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer = RedisServer.builder().port(Integer.parseInt(port)).setting("maxmemory 128M").build();
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}