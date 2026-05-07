package com.example.demo.controller.Points;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdatePointRequest {
    @NotNull(message = "reason cannot be null")
    private String reason;
    
    @Override
    public String toString() {
        return String.format("UpdatePointRequest {reason=%s}", reason);
    }
}
