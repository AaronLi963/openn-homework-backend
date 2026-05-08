package com.example.demo.mq.dto;

import com.example.demo.model.Point;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AddUserPointDto {
    private Long id;
    private String userId;
    private int amount;
    private String reason;
    private Date timestamp = new Date();

    public AddUserPointDto(Point p) {
        this.id = p.getId();
        this.userId = p.getUserId();
        this.amount = p.getAmount();
        this.reason = p.getReason();
    }

    public AddUserPointDto() {}


    @Override
    public String toString() {
        return String.format("AddUserPointDto {id=%d, userId=%s, amount=%d, reason=%s, timestamp=%s}", id, userId, amount, reason, timestamp);
    }
}
