package com.englishflow.event.controller;

import com.englishflow.event.dto.EventNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventWebSocketController {

    /**
     * Handle subscription to event updates
     */
    @SubscribeMapping("/event/{eventId}")
    public EventNotificationDTO onSubscribeToEvent(@DestinationVariable Long eventId) {
        log.info("Client subscribed to event: {}", eventId);
        return EventNotificationDTO.builder()
            .type("SUBSCRIPTION_CONFIRMED")
            .eventId(eventId)
            .message("Successfully subscribed to event updates")
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Handle messages sent to an event
     */
    @MessageMapping("/event/{eventId}/message")
    @SendTo("/topic/event/{eventId}")
    public EventNotificationDTO handleEventMessage(@DestinationVariable Long eventId, 
                                                   EventNotificationDTO message) {
        log.info("Received message for event {}: {}", eventId, message.getMessage());
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    /**
     * Handle ping/pong for connection health check
     */
    @MessageMapping("/event/ping")
    @SendTo("/topic/event/pong")
    public String handlePing() {
        return "pong";
    }
}
