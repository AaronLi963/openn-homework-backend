package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class TestRedisConfig {

    private RedisServer redisServer;

    public TestRedisConfig() throws IOException {
        // Use a random port or a fixed port that doesn't conflict
        this.redisServer = new RedisServer(6379);
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        try {
            redisServer.start();
        } catch (Exception e) {
            // If already started or port occupied, log and continue
            System.err.println("Embedded Redis failed to start: " + e.getMessage());
        }
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }
}
