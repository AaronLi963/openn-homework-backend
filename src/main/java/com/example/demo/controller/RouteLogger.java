package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class RouteLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            System.out.println(info + " -> " + method);
        });
    }
}
