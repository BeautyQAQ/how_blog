package com.liushao.config;

import com.liushao.filter.WebFilter;
import com.liushao.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class GatewayAccessTest {
    @Test void gatewayRejectsMissingInvalidAndInsufficientCredentialsAndPreservesPublicReads() {
        var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000);
        var filter = new WebFilter(jwt, new ObjectMapper());
        for (String header : new String[]{"", "Bearer invalid", "Bearer " + jwt.createJWT("reader", "reader", "user")}) {
            var builder = MockServerHttpRequest.post("/search/search/init");
            if (!header.isEmpty()) builder.header("Authorization", header);
            var exchange = MockServerWebExchange.from(builder); var called = new AtomicBoolean();
            filter.filter(exchange, next -> { called.set(true); return Mono.empty(); }).block();
            assertFalse(called.get());
            assertEquals(header.isEmpty() || header.equals("Bearer invalid") ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN,
                    exchange.getResponse().getStatusCode());
        }
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/article/article/public"));
        var called = new AtomicBoolean();
        filter.filter(exchange, next -> { called.set(true); return Mono.empty(); }).block();
        assertTrue(called.get());
    }
}
