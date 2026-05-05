package com.example.demo.service.dto;

import lombok.Getter;

@Getter
public class PointDto {
    private Long id;
    private String userId;
    private Integer amount;
    private String reason;

    public PointDto(Long id, String userId, Integer amount, String reason) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("PointDto {id=%d, userId=%s, amount=%d, reason=%s}", id, userId, amount, reason);
    }
}
