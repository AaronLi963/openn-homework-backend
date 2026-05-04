package com.example.demo.controller.Points;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.Response;

import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/points")
public class PointsController {
    
    @PostMapping
    public Response addPoints(@Valid @RequestBody AddPointsRequest request) {
        return Response.success("Points added successfully");
    }
}
