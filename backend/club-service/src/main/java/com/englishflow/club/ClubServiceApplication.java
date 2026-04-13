package com.englishflow.club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ClubServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClubServiceApplication.class, args);
    }
    
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
