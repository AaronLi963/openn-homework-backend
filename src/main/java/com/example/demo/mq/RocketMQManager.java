package com.example.demo.mq;

import com.example.demo.config.RocketMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

import java.util.HashMap;   
import java.util.ArrayList;

@Component
public class RocketMQManager {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQManager.class);

    @Autowired
    private RocketMQConfig rocketMQConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private DefaultMQProducer producer;
    private HashMap<String, ArrayList<DefaultMQPushConsumer>> consumersMap = new HashMap<>();

    public RocketMQManager(RocketMQConfig rocketMQConfig) {
        logger.info("RocketMQConfig: {}", rocketMQConfig);
        this.rocketMQConfig = rocketMQConfig;
        this.producer = getProducer();
    }

    public void sendMessage(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            producer.send(new Message(topic, json.getBytes()));
            logger.info("send message success {}", json);
        } catch (Exception e) {
            logger.error("Failed to send message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void registerConsumer(String groupId, String topic, MessageListener messageListener) {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupId);
            consumer.setNamesrvAddr(rocketMQConfig.getNameServer());
            consumer.subscribe(topic, "*");
            consumer.setMessageListener(messageListener);
            consumer.start();
            consumersMap.computeIfAbsent(groupId, k -> new ArrayList<>()).add(consumer);
            
            logger.info("Consumer: {} registered successfully", messageListener.getClass().getName());
        } catch (Exception e) {
            logger.error("Failed to register consumer", e);
            throw new RuntimeException("Failed to register consumer", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            // shutdown producer
            producer.shutdown();
            logger.info("Producer shutdown successfully");

            // shutdown consumers
            consumersMap.values().forEach(consumers -> consumers.forEach(consumer -> {
                try {
                    consumer.shutdown();
                } catch (Exception e) {
                    logger.error("Failed to shutdown consumer", e);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Failed to shutdown producer and consumers", e);
        }
    }


    private DefaultMQProducer getProducer() {
        try {
        DefaultMQProducer producer = new DefaultMQProducer("producer");
            producer.setNamesrvAddr(rocketMQConfig.getNameServer());
            producer.setSendMsgTimeout(10000);
            producer.start();
            logger.info("Producer started successfully");
            return producer;
        } catch (MQClientException e) {
            logger.error("Failed to create producer", e);
            throw new RuntimeException("Failed to create RocketMQ producer", e);
        }
    }
}
