package com.example.demo.controller.Points;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddPointsRequest {
    @NotNull(message = "userId cannot be null")
    private String userId;
    @NotNull(message = "amount cannot be null")
    private Integer amount;
    private String reason;
    
    @Override
    public String toString() {
        return String.format("AddPointsRequest {userId=%s, amount=%d, reason=%s}", userId, amount, reason);
    }
}
