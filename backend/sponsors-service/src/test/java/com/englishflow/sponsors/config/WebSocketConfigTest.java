package com.englishflow.sponsors.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private SimpleBrokerRegistration simpleBrokerRegistration;

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration endpointRegistration;

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    void configureMessageBroker_ShouldConfigureBrokerWithHeartbeat() {
        // Arrange
        when(messageBrokerRegistry.enableSimpleBroker(anyString())).thenReturn(simpleBrokerRegistration);
        when(simpleBrokerRegistration.setHeartbeatValue(any(long[].class))).thenReturn(simpleBrokerRegistration);
        when(simpleBrokerRegistration.setTaskScheduler(any())).thenReturn(simpleBrokerRegistration);

        // Act
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);

        // Assert
        verify(messageBrokerRegistry).enableSimpleBroker("/topic");
        verify(simpleBrokerRegistration).setHeartbeatValue(any(long[].class));
        verify(simpleBrokerRegistration).setTaskScheduler(any());
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void registerStompEndpoints_ShouldRegisterEndpointsWithSockJS() {
        // Arrange
        when(stompEndpointRegistry.addEndpoint(anyString())).thenReturn(endpointRegistration);
        when(endpointRegistration.setAllowedOriginPatterns(anyString())).thenReturn(endpointRegistration);
        when(endpointRegistration.withSockJS()).thenReturn(null);

        // Act
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);

        // Assert
        verify(stompEndpointRegistry, times(2)).addEndpoint("/ws");
        verify(endpointRegistration, times(2)).setAllowedOriginPatterns("*");
        verify(endpointRegistration, times(1)).withSockJS();
    }
}
