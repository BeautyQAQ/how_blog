package com.liushao;

import com.liushao.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author huangshen
 */

@SpringBootApplication
public class GatewayManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayManagerApplication.class, args);
    }

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil();
    }

}
