package com.example.demo.controller.Points;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPointsRequest {
    @NotNull(message = "userId cannot be null")
    private String userId;
    @NotNull(message = "amount cannot be null")
    private Integer amount;
    private String reason;
}
