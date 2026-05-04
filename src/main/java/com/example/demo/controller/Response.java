package com.example.demo.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private String status;
    private Integer errorCode;
    private String errorMessage;
    private Object data;
    
    public static Response success(Object data) {
        Response response = new Response();
        response.setStatus("success");
        response.setErrorCode(0);
        response.setErrorMessage(null);
        response.setData(data);
        return response;
    }

    public static Response error(Integer errorCode, String errorMessage) {
        Response response = new Response();
        response.setStatus("error");
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setData(null);
        return response;
    }
}
