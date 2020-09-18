package com.liushao.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
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
 * <p>
 * 全局Filter，统一处理会员登录与外部不允许访问的服务
 * </p>
 */
@Component
public class ManagerFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("manager过滤器");
        ServerHttpRequest request = exchange.getRequest();
        if(request.getMethod().equals("OPTIONS")){
            return chain.filter(exchange);
        }
        String url = request.getURI().getPath();
        if(url.indexOf("/admin/login")>0){
            System.out.println("登陆页面"+url);
            return chain.filter(exchange);
        }
        //获取头信息
        String authHeader = request.getHeaders().getFirst("Authorization");
        if(authHeader!=null && authHeader.startsWith("Bearer ") ){
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.parseJWT(token);
            if(claims!=null){
                if("admin".equals(claims.get("roles"))){
                    ServerHttpRequest host = exchange.getRequest().mutate().header("Authorization", authHeader).build();
                    //将现在的request 变成 change对象
                    exchange.mutate().request(host).build();
                    System.out.println("token 验证通过，添加了头信息"+authHeader);
                    return chain.filter(exchange);
                }
            }
        }
        ServerHttpResponse response = exchange.getResponse();
        return out(response);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> out(ServerHttpResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        Result result = new Result(false, StatusCode.ACCESSERROR,"鉴权失败");
        String jsonStr = "";
        try {
            jsonStr = objectMapper.writeValueAsString(result);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        byte[] bits = jsonStr.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
