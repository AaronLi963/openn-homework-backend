package com.example.demo.controller;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("Invalid input");
        return ResponseEntity.badRequest().body(Response.error(Error.ERROR_CODE_INVALID_INPUT, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
            .body(Response.error(Error.ERROR_CODE_INTERNAL_ERROR, "Invalid request body"));
    }
}
