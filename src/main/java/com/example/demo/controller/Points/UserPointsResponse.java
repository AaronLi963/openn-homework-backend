package com.example.demo.controller.Points;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPointsResponse {
    private String userId;
    private Integer totalPoints;

    public UserPointsResponse(String userId, Integer totalPoints) {
        this.userId = userId;
        this.totalPoints = totalPoints;
    }
}
