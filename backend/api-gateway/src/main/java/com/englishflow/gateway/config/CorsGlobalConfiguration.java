package com.englishflow.gateway.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsGlobalConfiguration {
    // CORS est maintenant géré dans application.yml via spring.cloud.gateway.globalcors
    // pour éviter les conflits de headers "Access-Control-Allow-Origin cannot contain more than one origin"
}
