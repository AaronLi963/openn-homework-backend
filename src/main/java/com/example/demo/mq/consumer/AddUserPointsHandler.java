package com.example.demo.mq.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.mq.RocketMQManager;
import com.example.demo.mq.RocketMQTopics;
import com.example.demo.mq.dto.AddUserPointDto;
import com.example.demo.util.JsonUtil;

import jakarta.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AddUserPointsHandler implements MessageListenerConcurrently {
    private static final Logger logger = LoggerFactory.getLogger(AddUserPointsHandler.class);

    @Autowired
    private RocketMQManager rocketMQManager;

    @PostConstruct
    public void registerConsumer() {
        rocketMQManager.registerConsumer("add_user_points_handler", RocketMQTopics.TOPIC_USER_POINTS_TOPIC, this);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt msg: msgs) {
                String body = new String(msg.getBody());
                AddUserPointDto dto = JsonUtil.toObject(body, AddUserPointDto.class);
                logger.info("get add user point event: {}", dto);
            }
        } catch (Exception e) {
            logger.error("Failed to consume messages, {}", e);
        }
        logger.info("Successfully consume {} messages", msgs.size());
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
