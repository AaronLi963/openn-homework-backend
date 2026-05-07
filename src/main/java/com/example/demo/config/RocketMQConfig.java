package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "spring.rocketmq")
@Configuration
public class RocketMQConfig {
    @Value("${spring.rocketmq.name-server}")
    private String nameServer;

    @Override
    public String toString() {
        return String.format("RocketMQConfig {nameServer=%s}", nameServer);
    }
}
