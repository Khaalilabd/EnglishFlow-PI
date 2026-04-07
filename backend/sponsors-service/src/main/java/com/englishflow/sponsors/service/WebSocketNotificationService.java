package com.englishflow.sponsors.service;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.dto.SponsorNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TOPIC_SPONSORS = "/topic/sponsors";
    
    public void notifySponsorCreated(SponsorDTO sponsor) {
        try {
            SponsorNotificationDTO notification = SponsorNotificationDTO.builder()
                    .type("CREATED")
                    .sponsorId(sponsor.getId())
                    .sponsorName(sponsor.getName())
                    .message("New sponsor created: " + sponsor.getName())
                    .timestamp(LocalDateTime.now())
                    .sponsor(sponsor)
                    .build();
            
            messagingTemplate.convertAndSend(TOPIC_SPONSORS, notification);
            log.info("WebSocket notification sent: Sponsor created - {}", sponsor.getName());
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for sponsor creation", e);
        }
    }
    
    public void notifySponsorUpdated(SponsorDTO sponsor) {
        try {
            SponsorNotificationDTO notification = SponsorNotificationDTO.builder()
                    .type("UPDATED")
                    .sponsorId(sponsor.getId())
                    .sponsorName(sponsor.getName())
                    .message("Sponsor updated: " + sponsor.getName())
                    .timestamp(LocalDateTime.now())
                    .sponsor(sponsor)
                    .build();
            
            messagingTemplate.convertAndSend(TOPIC_SPONSORS, notification);
            log.info("WebSocket notification sent: Sponsor updated - {}", sponsor.getName());
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for sponsor update", e);
        }
    }
    
    public void notifySponsorDeleted(Long sponsorId, String sponsorName) {
        try {
            SponsorNotificationDTO notification = SponsorNotificationDTO.builder()
                    .type("DELETED")
                    .sponsorId(sponsorId)
                    .sponsorName(sponsorName)
                    .message("Sponsor deleted: " + sponsorName)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            messagingTemplate.convertAndSend(TOPIC_SPONSORS, notification);
            log.info("WebSocket notification sent: Sponsor deleted - {}", sponsorName);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for sponsor deletion", e);
        }
    }
}
