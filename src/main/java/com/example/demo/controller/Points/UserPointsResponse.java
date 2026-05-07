package com.example.demo.controller.Points;

import lombok.Getter;

@Getter
public class UserPointsResponse {
    private final String userId;
    private final Integer totalPoints;

    public UserPointsResponse(String userId, Integer totalPoints) {
        this.userId = userId;
        this.totalPoints = totalPoints;
    }
}
