package com.englishflow.courses.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS is handled by API Gateway
    // No need to configure CORS here to avoid duplicate headers
}
