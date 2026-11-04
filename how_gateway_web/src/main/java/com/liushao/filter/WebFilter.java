package com.liushao.filter;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 权限设计还有问题，待完善
 * @author huangshen
 */
@Component
@Slf4j
public class WebFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("web过滤器");
        ServerHttpRequest request = exchange.getRequest();
        // 跨域请求允许通过
        if (org.springframework.http.HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }
        // 登录请求允许通过
        String url = request.getURI().getPath();
        if (url.contains("/admin/login") || url.contains("/user/login")) {
            log.info("登陆页面:{}", url);
            return chain.filter(exchange);
        }
        // 以下请求放行，如果存在Authorization，则下放到模块中验证，同时放行/v3/api-docs接口
        if (url.contains("/article") || url.contains("/search") || url.contains("/channel") || url.contains("/city")
                || url.contains("/column") || url.contains("/comment") || url.contains("/label") || url.contains("/v3/api-docs")
                || url.startsWith("/swagger-ui") || url.startsWith("/webjars/")
                || url.contains("/problem") || url.contains("/reply") || url.contains("/spit")) {
            log.info("请求url:{}", url);
            // 获取头信息
            String authorization = request.getHeaders().getFirst("Authorization");
            // Authorization信息存在，转发到模块中校验
            if (authorization != null) {
                ServerHttpRequest host = exchange.getRequest().mutate().header("Authorization", authorization).build();
                // 将现在的request 变成 change对象
                exchange.mutate().request(host).build();
            }
            return chain.filter(exchange);
        }
        // 到这里，剩下的接口则需要管理员权限，否则会验证失败
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.parseJWT(token);
            if (claims != null) {
                if ("admin".equals(claims.get("roles"))) {
                    log.info("管理员 token 验证通过");
                    return chain.filter(exchange);
                }
            }
        }
        log.info("token 验证失败，无权访问");
        ServerHttpResponse response = exchange.getResponse();
        return out(response);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> out(ServerHttpResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        Result result = new Result(false, StatusCode.ACCESSERROR, "鉴权失败");
        String jsonStr = "";
        try {
            jsonStr = objectMapper.writeValueAsString(result);
        } catch (JacksonException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        byte[] bits = jsonStr.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
