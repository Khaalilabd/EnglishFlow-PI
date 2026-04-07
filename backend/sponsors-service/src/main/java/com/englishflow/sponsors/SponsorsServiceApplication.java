package com.englishflow.sponsors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SponsorsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SponsorsServiceApplication.class, args);
    }
}
