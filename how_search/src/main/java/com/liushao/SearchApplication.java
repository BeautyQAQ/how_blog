package com.liushao;

import com.liushao.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author huangshen
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
    @Bean
    public IdWorker idWorkker(){
        return new IdWorker(1, 1);
    }

}
