package com.example.demo.service;

import com.example.demo.model.Point;
import com.example.demo.mq.RocketMQManager;
import com.example.demo.mq.RocketMQTopics;
import com.example.demo.mq.dto.AddUserPointDto;
import com.example.demo.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private RocketMQManager rocketMQManager;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void setUp() {
        // Mocking the fluent API of RedisTemplate
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void testAddPoints_Success() throws Exception {
        // Given
        String userId = "user1";
        int amount = 100;
        String reason = "test";
        
        when(valueOperations.get(anyString())).thenReturn(null); // Cache miss
        when(pointRepository.sumAmountByUserId(userId)).thenReturn(100);

        // When
        Integer totalPoints = pointService.addPoints(userId, amount, reason);

        // Then
        assertEquals(100, totalPoints);
        verify(pointRepository, times(1)).save(any(Point.class));
        verify(rocketMQManager, times(1)).sendMessage(eq(RocketMQTopics.TOPIC_USER_POINTS_TOPIC), any(AddUserPointDto.class));
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    void testGetUserPoints_CacheHit() throws Exception {
        // Given
        String userId = "user1";
        when(valueOperations.get("points:user:" + userId)).thenReturn(500);

        // When
        Integer totalPoints = pointService.getUserPoints(userId);

        // Then
        assertEquals(500, totalPoints);
        verify(pointRepository, never()).sumAmountByUserId(anyString());
    }

    @Test
    void testGetUserPoints_CacheMiss() throws Exception {
        // Given
        String userId = "user1";
        when(valueOperations.get(anyString())).thenReturn(null);
        when(pointRepository.sumAmountByUserId(userId)).thenReturn(300);

        // When
        Integer totalPoints = pointService.getUserPoints(userId);

        // Then
        assertEquals(300, totalPoints);
        verify(valueOperations, times(1)).set(eq("points:user:" + userId), eq(300));
    }
}
