package com.englishflow.club.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {
    
    @Value("${internal.service.key}")
    private String internalServiceKey;
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Ajouter le header d'authentification inter-services
            requestTemplate.header("X-Internal-Service-Key", internalServiceKey);
            log.info("🔑 [FEIGN] Added X-Internal-Service-Key header to request: {} {}", 
                    requestTemplate.method(), requestTemplate.url());
            log.info("🔑 [FEIGN] Internal service key (first 10 chars): {}...", 
                    internalServiceKey != null && internalServiceKey.length() > 10 
                        ? internalServiceKey.substring(0, 10) 
                        : "NULL");
        };
    }
}
