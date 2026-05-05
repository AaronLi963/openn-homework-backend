package com.example.demo.service;

import com.example.demo.model.Point;
import com.example.demo.repository.PointRepository;
import com.example.demo.service.dto.PointDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;

    public PointDto addPoints(String userId, int amount, String reason) throws Exception {
        Point point = new Point();
        point.setUserId(userId);
        point.setAmount(amount);
        point.setReason(reason);
        try {
            pointRepository.save(point);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add points", e);
        }
        return convertToDto(point);
    }
    
    private PointDto convertToDto(Point point) {
        return new PointDto(point.getId(), point.getUserId(), point.getAmount(), point.getReason());
    }
}
