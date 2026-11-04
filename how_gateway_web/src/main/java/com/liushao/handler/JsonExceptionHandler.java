package com.liushao.handler;

import com.liushao.entity.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/** Preserves the gateway's JSON error contract across Spring Boot upgrades. */
public class JsonExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    public JsonExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable error) {
        var response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(error);
        }
        byte[] json = objectMapper.writeValueAsBytes(new Result(false, 20005, "网关失败"));
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(json)));
    }
}
