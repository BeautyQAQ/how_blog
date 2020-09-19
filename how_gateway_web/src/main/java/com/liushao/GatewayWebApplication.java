package com.liushao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author huangshen
 */

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class, args);
    }
}
