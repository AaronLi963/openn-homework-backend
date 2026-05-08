package com.example.demo.service.dto;

import lombok.Getter;

@Getter
public class LeaderboardDto {
    String userId;
    Long total;

    public LeaderboardDto(String userId, Long total) {
        this.userId = userId;
        this.total = total;
    }

    @Override
    public String toString() {
        return String.format("LeaderboardDto {userId=%s, total=%d}", userId, total);
    }
}
