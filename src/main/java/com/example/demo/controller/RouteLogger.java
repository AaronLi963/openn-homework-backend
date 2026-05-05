package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RouteLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    private static final Logger logger = LoggerFactory.getLogger(RouteLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Logging routes...");
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            logger.info("{} -> {}", info, method);
        });
        logger.info("Routes logged successfully");
    }
}
