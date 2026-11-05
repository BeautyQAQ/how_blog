package com.liushao.filter;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.security.AccessPolicy;
import com.liushao.security.JwtAccess;
import com.liushao.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component
public class WebFilter implements GlobalFilter, Ordered {
    private final JwtUtil jwt;
    private final ObjectMapper mapper;
    public WebFilter(JwtUtil jwt, ObjectMapper mapper) { this.jwt = jwt; this.mapper = mapper; }
    @Override public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        try {
            JwtAccess.authorize(jwt, AccessPolicy.gateway(request.getMethod().name(), request.getURI().getPath()),
                    request.getHeaders().getFirst("Authorization"));
            return chain.filter(exchange);
        } catch (JwtAccess.Rejected e) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatusCode.valueOf(e.status()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] json = mapper.writeValueAsBytes(new Result(false, StatusCode.ACCESSERROR, e.getMessage()));
            return response.writeWith(Mono.just(response.bufferFactory().wrap(json)));
        }
    }
    @Override public int getOrder() { return -100; }
}
