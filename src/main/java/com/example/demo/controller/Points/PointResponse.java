package com.example.demo.controller.Points;

import lombok.Getter;

@Getter
public class PointResponse {
    private final Long id;
    private final String userId;
    private final String reason;
    private final Integer amount;

    public PointResponse(Long id, String userId, String reason, Integer amount) {
        this.id = id;
        this.userId = userId;
        this.reason = reason;
        this.amount = amount;
    }
}
