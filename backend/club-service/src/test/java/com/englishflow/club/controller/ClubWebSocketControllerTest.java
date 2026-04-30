package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ClubWebSocketControllerTest {

    @InjectMocks
    private ClubWebSocketController clubWebSocketController;

    @Test
    void onSubscribeToClub_ShouldReturnSubscriptionConfirmation() {
        // Given
        Long clubId = 1L;

        // When
        ClubNotificationDTO result = clubWebSocketController.onSubscribeToClub(clubId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("SUBSCRIPTION_CONFIRMED");
        assertThat(result.getClubId()).isEqualTo(clubId);
        assertThat(result.getMessage()).isEqualTo("Successfully subscribed to club updates");
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getTimestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void onSubscribeToClub_ShouldHandleDifferentClubIds() {
        // Given
        Long clubId = 999L;

        // When
        ClubNotificationDTO result = clubWebSocketController.onSubscribeToClub(clubId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getClubId()).isEqualTo(999L);
        assertThat(result.getType()).isEqualTo("SUBSCRIPTION_CONFIRMED");
    }

    @Test
    void handleClubMessage_ShouldReturnMessageWithTimestamp() {
        // Given
        Long clubId = 1L;
        ClubNotificationDTO message = ClubNotificationDTO.builder()
            .type("MEMBER_JOINED")
            .clubId(clubId)
            .message("New member joined the club")
            .build();

        // When
        ClubNotificationDTO result = clubWebSocketController.handleClubMessage(clubId, message);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("MEMBER_JOINED");
        assertThat(result.getClubId()).isEqualTo(clubId);
        assertThat(result.getMessage()).isEqualTo("New member joined the club");
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getTimestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void handleClubMessage_ShouldUpdateTimestamp() {
        // Given
        Long clubId = 2L;
        LocalDateTime oldTimestamp = LocalDateTime.now().minusHours(1);
        ClubNotificationDTO message = ClubNotificationDTO.builder()
            .type("CLUB_UPDATED")
            .clubId(clubId)
            .message("Club information updated")
            .timestamp(oldTimestamp)
            .build();

        // When
        ClubNotificationDTO result = clubWebSocketController.handleClubMessage(clubId, message);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getTimestamp()).isAfter(oldTimestamp);
    }

    @Test
    void handlePing_ShouldReturnPong() {
        // When
        String result = clubWebSocketController.handlePing();

        // Then
        assertThat(result).isEqualTo("pong");
    }
}
