package com.englishflow.messaging.config;

import com.englishflow.messaging.constants.MessagingConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Value("${websocket.allowed-origins:http://localhost:4200,http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;
    
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    
    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Create TaskScheduler for heartbeat
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-");
        taskScheduler.initialize();
        
        // Enable a simple in-memory broker with heartbeat
        config.enableSimpleBroker(MessagingConstants.WS_TOPIC_PREFIX, "/queue")
              .setHeartbeatValue(new long[] {10000, 10000}) // Heartbeat every 10 seconds
              .setTaskScheduler(taskScheduler);
        
        config.setApplicationDestinationPrefixes(MessagingConstants.WS_APP_PREFIX);
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint with SockJS fallback
        // Use wildcard to avoid CORS duplication with API Gateway
        registry.addEndpoint(MessagingConstants.WS_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000) // SockJS heartbeat
                .setDisconnectDelay(5000); // Delay before disconnect
        
        // Also register without SockJS for native WebSocket clients
        registry.addEndpoint(MessagingConstants.WS_ENDPOINT)
                .setAllowedOriginPatterns("*");
    }
}
