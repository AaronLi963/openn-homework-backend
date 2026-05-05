package com.example.demo.controller.Points;

import com.example.demo.controller.Error;
import com.example.demo.controller.Response;
import com.example.demo.service.PointService;
import com.example.demo.service.dto.PointDto;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointService pointService;
    
    @PostMapping
    public Response addPoints(@Valid @RequestBody PointDto request) {
        try {
            PointDto point = pointService.addPoints(request.getUserId(), request.getAmount(), request.getReason());
            return Response.success(point);
        } catch (Exception e) {
            return Response.error(Error.ERROR_CODE_INTERNAL_ERROR, e.getMessage());
        }
    }
}
