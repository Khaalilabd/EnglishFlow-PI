package com.englishflow.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
public class SseGatewayConfig {

    @Bean
    public RouteLocator sseRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("sse-notifications", r -> r
                        .path("/api/complaints/notifications/stream/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f
                                .stripPrefix(1)
                                // Headers essentiels pour SSE
                                .setResponseHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-transform")
                                .setResponseHeader(HttpHeaders.CONNECTION, "keep-alive")
                                .setResponseHeader("X-Accel-Buffering", "no")
                                // Désactiver le buffering pour permettre le streaming
                                .setResponseHeader(HttpHeaders.CONTENT_TYPE, "text/event-stream")
                        )
                        .uri("lb://complaints-service")
                )
                .build();
    }
}
