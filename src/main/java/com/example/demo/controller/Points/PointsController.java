package com.example.demo.controller.Points;

import com.example.demo.controller.Error;
import com.example.demo.controller.Response;
import com.example.demo.model.Point;
import com.example.demo.service.PointService;
import com.example.demo.service.dto.LeaderboardDto;
import com.example.demo.service.dto.PointDto;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @PutMapping("/{pointId}")
    public Response updatePoint(@PathVariable Long pointId, @Valid @RequestBody UpdatePointRequest request) {
        try {
            logger.info("update reason for point: {}, reason: {}", pointId, request.getReason());

            // according to the request, we don't allow to update the amount
            Point point = pointService.updatePoint(pointId, null, request.getReason());
            PointResponse pointResponse = new PointResponse(point.getId(), point.getUserId(), point.getReason(), point.getAmount());
            return Response.success(pointResponse);
        } catch (Exception e) {
            logger.error("Failed to update point: {}, error: {}", pointId, e.getMessage());
            return Response.error(Error.ERROR_CODE_INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    public Response getLeaderboard() {
        Integer leaderboardSize = 10;
        try {
            logger.info("Getting leaderboard");
            List<LeaderboardDto> leadingUsers = pointService.getLeaderboard(leaderboardSize);
            return Response.success(leadingUsers);
        } catch (Exception e) {
            logger.error("Failed to get leaderboard, error: {}", e.getMessage());
            return Response.error(Error.ERROR_CODE_INTERNAL_ERROR, e.getMessage());
        }
    }
}
