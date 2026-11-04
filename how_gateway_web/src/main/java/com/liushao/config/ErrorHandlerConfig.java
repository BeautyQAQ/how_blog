package com.liushao.config;

import com.liushao.handler.JsonExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class ErrorHandlerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    ErrorWebExceptionHandler errorWebExceptionHandler(ObjectMapper objectMapper) {
        return new JsonExceptionHandler(objectMapper);
    }
}
