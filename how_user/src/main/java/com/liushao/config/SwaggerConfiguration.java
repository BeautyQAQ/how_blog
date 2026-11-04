package com.liushao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    OpenAPI applicationOpenApi() {
        return new OpenAPI().info(new Info().title("怎样博客用户服务").version("1.0"));
    }
}
