package com.englishflow.sponsors.service;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.dto.SponsorNotificationDTO;
import com.englishflow.sponsors.entity.Sponsor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationService webSocketNotificationService;

    @Test
    void notifySponsorCreated_ShouldSendNotification() {
        SponsorDTO sponsor = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .contributionAmount(1500.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .build();

        webSocketNotificationService.notifySponsorCreated(sponsor);

        ArgumentCaptor<SponsorNotificationDTO> captor = ArgumentCaptor.forClass(SponsorNotificationDTO.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/sponsors"), captor.capture());

        SponsorNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("CREATED");
        assertThat(notification.getSponsorId()).isEqualTo(1L);
        assertThat(notification.getSponsorName()).isEqualTo("Tech Corp");
        assertThat(notification.getMessage()).contains("New sponsor created");
        assertThat(notification.getSponsor()).isEqualTo(sponsor);
    }

    @Test
    void notifySponsorUpdated_ShouldSendNotification() {
        SponsorDTO sponsor = SponsorDTO.builder()
                .id(2L)
                .name("Business Inc")
                .contributionAmount(800.0)
                .level(Sponsor.SponsorLevel.SILVER)
                .build();

        webSocketNotificationService.notifySponsorUpdated(sponsor);

        ArgumentCaptor<SponsorNotificationDTO> captor = ArgumentCaptor.forClass(SponsorNotificationDTO.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/sponsors"), captor.capture());

        SponsorNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("UPDATED");
        assertThat(notification.getSponsorId()).isEqualTo(2L);
        assertThat(notification.getSponsorName()).isEqualTo("Business Inc");
        assertThat(notification.getMessage()).contains("Sponsor updated");
        assertThat(notification.getSponsor()).isEqualTo(sponsor);
    }

    @Test
    void notifySponsorDeleted_ShouldSendNotification() {
        webSocketNotificationService.notifySponsorDeleted(3L, "Deleted Sponsor");

        ArgumentCaptor<SponsorNotificationDTO> captor = ArgumentCaptor.forClass(SponsorNotificationDTO.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/sponsors"), captor.capture());

        SponsorNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("DELETED");
        assertThat(notification.getSponsorId()).isEqualTo(3L);
        assertThat(notification.getSponsorName()).isEqualTo("Deleted Sponsor");
        assertThat(notification.getMessage()).contains("Sponsor deleted");
        assertThat(notification.getSponsor()).isNull();
    }

    @Test
    void notifySponsorCreated_WhenExceptionOccurs_ShouldHandleGracefully() {
        SponsorDTO sponsor = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .build();

        doThrow(new RuntimeException("WebSocket error"))
                .when(messagingTemplate).convertAndSend(eq("/topic/sponsors"), any(SponsorNotificationDTO.class));

        // Should not throw exception
        webSocketNotificationService.notifySponsorCreated(sponsor);

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/sponsors"), any(SponsorNotificationDTO.class));
    }
}
