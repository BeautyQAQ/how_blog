package com.liushao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import com.liushao.security.AccessPolicy;
import com.liushao.util.JwtUtil;
import tools.jackson.databind.ObjectMapper;

/**
 * 安全配置类
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwt, ObjectMapper mapper) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(request -> AccessPolicy.required(AccessPolicy.Service.USER,
                                request.getMethod(), request.getRequestURI().substring(request.getContextPath().length()))
                                == AccessPolicy.Access.PUBLIC).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwt, mapper), AuthorizationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
