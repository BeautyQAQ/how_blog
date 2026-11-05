package com.liushao.config;

import com.liushao.security.AccessPolicy;
import com.liushao.security.ApiAuthorizationInterceptor;
import com.liushao.util.JwtUtil;
import feign.RequestInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtUtil.class)
public class SearchSecurityConfig implements WebMvcConfigurer {
    private final JwtUtil jwt;
    public SearchSecurityConfig(JwtUtil jwt) { this.jwt = jwt; }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAuthorizationInterceptor(jwt, AccessPolicy.Service.SEARCH)).addPathPatterns("/**");
    }
    @Bean RequestInterceptor forwardAuthorization() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String header = attributes.getRequest().getHeader("Authorization");
                if (header != null) template.header("Authorization", header);
            }
        };
    }
}
