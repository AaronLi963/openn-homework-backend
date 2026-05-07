package com.example.demo.service;

import com.example.demo.model.Point;
import com.example.demo.mq.RocketMQManager;
import com.example.demo.mq.RocketMQTopics;
import com.example.demo.mq.dto.AddUserPointDto;
import com.example.demo.repository.PointRepository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class PointService {
    private static final String CACHE_KEY_PREFIX = "points:user:";

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    @Autowired
    private RocketMQManager rocketMQManager;

    @Transactional
    public Integer addPoints(String userId, int amount, String reason) throws Exception {
        Point point = new Point();
        point.setUserId(userId);
        point.setAmount(amount);
        point.setReason(reason);
        try {
            pointRepository.save(point);
            logger.info("Points added successfully: {}", point);

            // send RocketMQ message
            AddUserPointDto message = new AddUserPointDto(point);
            rocketMQManager.sendMessage(RocketMQTopics.TOPIC_USER_POINTS_TOPIC, message);

            clearCachedUserPoints(userId);
            return getUserPoints(userId);
        } catch (Exception e) {
            logger.error("Failed to add points, input: {}, error: {}", point, e.getMessage());
            throw new RuntimeException("Failed to add points", e);
        }
    }

    public Integer getUserPoints(String userId) throws Exception {
        try {
            Integer cachedPoints = getCachedUserPoints(userId);
            // hit cache, return cached points
            if (cachedPoints != null) {
                return cachedPoints;
            }

            Integer totalPoints = pointRepository.sumAmountByUserId(userId);
            if (totalPoints == null) {
                logger.info("No points found for user: {}", userId);
                totalPoints = 0;
            }

            // cache user points after getting points from DB
            cacheUserPoints(userId, totalPoints);
            return totalPoints;
        } catch (Exception e) {
            logger.error("Failed to get user points, userId: {}, error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get user points", e);
        }
    }


    public Point updatePoint(Long pointId, Integer amount, String reason) {
        try {
            Optional<Point> result = pointRepository.findById(pointId);
            if (result.isEmpty()) {
                logger.error("failed to get point: {}", pointId);
                return null;
            }

            Point point = result.get();
            if (amount != null) {
                point.setAmount(amount);
            }

            if (reason != null) {
                point.setReason(reason);
            }
            pointRepository.save(point);
            clearCachedUserPoints(point.getUserId());
            return point;
        } catch (Exception e) {
            logger.error("Failed to update point: {}, error: {}", pointId, e.getMessage());
            throw new RuntimeException("Failed to update points", e);
        }
    }


    private void cacheUserPoints(String userId, Integer points) {
        String key = getCacheKey(userId);
        try {
            logger.info("Cache user points: userId: {}, points: {}", userId, points);
            redisTemplate.opsForValue().set(key, points);
        } catch (Exception e) {
            // no need to throw exception, just log error
            logger.error("Failed to cache user points, userId: {}, error: {}", userId, e);
        }
    }

    private Integer getCachedUserPoints(String userId) {
        try {
            String key = getCacheKey(userId);
            Integer points = (Integer) redisTemplate.opsForValue().get(key);
            if (points == null) {
                logger.info("No cached user points found for user: {}", userId);
                return null;
            }
            return points;
        } catch (Exception e) {
            // no need to throw exception, just log error
            logger.error("Failed to get cached user points, userId: {}, error: {}", userId, e);
            return null;
        }
    }

    private void clearCachedUserPoints(String userId) {
        String key = getCacheKey(userId);
        try {
            logger.info("Clear cached user points: userId: {}", userId);
            redisTemplate.delete(key);
        } catch (Exception e) {
            // no need to throw exception, just log error
            logger.error("Failed to clear cached user points, userId: {}, error: {}", userId, e);
        }
    }

    private String getCacheKey(String userId) {
        return CACHE_KEY_PREFIX + userId;
    }
}
