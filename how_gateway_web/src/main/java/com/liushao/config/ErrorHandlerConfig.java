package com.liushao.config;

import com.liushao.handler.JsonExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class ErrorHandlerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    WebExceptionHandler errorWebExceptionHandler(ObjectMapper objectMapper) {
        return new JsonExceptionHandler(objectMapper);
    }
}
