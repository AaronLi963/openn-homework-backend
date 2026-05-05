package com.example.demo.service;

import com.example.demo.model.Point;
import com.example.demo.repository.PointRepository;
import com.example.demo.service.dto.PointDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;

    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    public PointDto addPoints(String userId, int amount, String reason) throws Exception {
        Point point = new Point();
        point.setUserId(userId);
        point.setAmount(amount);
        point.setReason(reason);
        try {
            pointRepository.save(point);
            logger.info("Points added successfully: {}", point);
        } catch (Exception e) {
            logger.error("Failed to add points, input: {}, error: {}", point, e.getMessage());
            throw new RuntimeException("Failed to add points", e);
        }
        return convertToDto(point);
    }
    
    private PointDto convertToDto(Point point) {
        return new PointDto(point.getId(), point.getUserId(), point.getAmount(), point.getReason());
    }
}
