package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubNotificationDTO;
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
public class ClubWebSocketController {

    /**
     * Handle subscription to club updates
     */
    @SubscribeMapping("/club/{clubId}")
    public ClubNotificationDTO onSubscribeToClub(@DestinationVariable Long clubId) {
        log.info("Client subscribed to club: {}", clubId);
        return ClubNotificationDTO.builder()
            .type("SUBSCRIPTION_CONFIRMED")
            .clubId(clubId)
            .message("Successfully subscribed to club updates")
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Handle messages sent to a club
     */
    @MessageMapping("/club/{clubId}/message")
    @SendTo("/topic/club/{clubId}")
    public ClubNotificationDTO handleClubMessage(@DestinationVariable Long clubId, 
                                                 ClubNotificationDTO message) {
        log.info("Received message for club {}: {}", clubId, message.getMessage());
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    /**
     * Handle ping/pong for connection health check
     */
    @MessageMapping("/club/ping")
    @SendTo("/topic/club/pong")
    public String handlePing() {
        return "pong";
    }
}
