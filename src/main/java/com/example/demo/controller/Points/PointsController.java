package com.example.demo.controller.Points;

import com.example.demo.controller.Error;
import com.example.demo.controller.Response;
import com.example.demo.service.PointService;
import com.example.demo.service.dto.PointDto;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointService pointService;

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);
    
    @PostMapping
    public Response addPoints(@Valid @RequestBody AddPointsRequest request) {
        try {
            logger.info("Adding points for user: {}", request.getUserId());
            Integer totalPoints = pointService.addPoints(request.getUserId(), request.getAmount(), request.getReason());
            logger.info("Points added successfully for user: {}", request.getUserId());
            return Response.success(new UserPointsResponse(request.getUserId(), totalPoints));
        } catch (Exception e) {
            logger.error("Failed to add points for user: {}, error: {}", request.getUserId(), e.getMessage());
            return Response.error(Error.ERROR_CODE_INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public Response getUserPoints(@PathVariable String userId) {
        try {
            logger.info("Getting points for user: {}", userId);
            Integer totalPoints = pointService.getUserPoints(userId);
            logger.info("Points fetched successfully for user: {}", userId);
            return Response.success(new UserPointsResponse(userId, totalPoints));
        } catch (Exception e) {
            logger.error("Failed to get points for user: {}, error: {}", userId, e.getMessage());
            return Response.error(Error.ERROR_CODE_INTERNAL_ERROR, e.getMessage());
        }
    }
}
