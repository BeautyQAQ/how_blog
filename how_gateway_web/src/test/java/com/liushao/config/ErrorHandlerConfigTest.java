package com.liushao.config;

import com.liushao.handler.JsonExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.webflux.autoconfigure.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerConfigTest {
    @Test
    void customHandlerLetsBootErrorAutoConfigurationBackOff() {
        new ReactiveWebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ErrorWebFluxAutoConfiguration.class))
                .withUserConfiguration(ErrorHandlerConfig.class)
                .withBean(ObjectMapper.class, ObjectMapper::new)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(ErrorWebExceptionHandler.class);
                    assertThat(context.getBean(ErrorWebExceptionHandler.class))
                            .isInstanceOf(JsonExceptionHandler.class);
                });
    }
}
