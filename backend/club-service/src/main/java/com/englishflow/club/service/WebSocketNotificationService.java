package com.englishflow.club.service;

import com.englishflow.club.dto.ClubNotificationDTO;
import com.englishflow.club.dto.MemberActivityDTO;
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

    /**
     * Send notification to all subscribers of a specific club
     */
    public void sendClubNotification(Long clubId, ClubNotificationDTO notification) {
        log.info("Sending WebSocket notification to club {}: {}", clubId, notification.getType());
        messagingTemplate.convertAndSend("/topic/club/" + clubId, notification);
    }

    /**
     * Send notification to all clubs (broadcast)
     */
    public void sendGlobalClubNotification(ClubNotificationDTO notification) {
        log.info("Sending global WebSocket notification: {}", notification.getType());
        messagingTemplate.convertAndSend("/topic/clubs", notification);
    }

    /**
     * Send member activity notification
     */
    public void sendMemberActivity(Long clubId, MemberActivityDTO activity) {
        log.info("Sending member activity to club {}: {}", clubId, activity.getActivityType());
        messagingTemplate.convertAndSend("/topic/club/" + clubId + "/members", activity);
    }

    /**
     * Send notification to a specific user
     */
    public void sendUserNotification(Long userId, ClubNotificationDTO notification) {
        log.info("Sending WebSocket notification to user {}: {}", userId, notification.getType());
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }

    /**
     * Helper method to create and send club created notification
     */
    public void notifyClubCreated(Long clubId, String clubName) {
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("CLUB_CREATED")
            .clubId(clubId)
            .clubName(clubName)
            .message("New club '" + clubName + "' has been created")
            .timestamp(LocalDateTime.now())
            .build();
        sendGlobalClubNotification(notification);
    }

    /**
     * Helper method to create and send club updated notification
     */
    public void notifyClubUpdated(Long clubId, String clubName) {
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("CLUB_UPDATED")
            .clubId(clubId)
            .clubName(clubName)
            .message("Club '" + clubName + "' has been updated")
            .timestamp(LocalDateTime.now())
            .build();
        sendClubNotification(clubId, notification);
    }

    /**
     * Helper method to create and send member joined notification
     */
    public void notifyMemberJoined(Long clubId, String clubName, Long userId, String userName, String role) {
        MemberActivityDTO activity = MemberActivityDTO.builder()
            .activityType("JOINED")
            .clubId(clubId)
            .userId(userId)
            .userName(userName)
            .role(role)
            .timestamp(LocalDateTime.now())
            .build();
        sendMemberActivity(clubId, activity);

        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("MEMBER_JOINED")
            .clubId(clubId)
            .clubName(clubName)
            .message(userName + " joined the club")
            .data(activity)
            .timestamp(LocalDateTime.now())
            .build();
        sendClubNotification(clubId, notification);
    }

    /**
     * Helper method to create and send member left notification
     */
    public void notifyMemberLeft(Long clubId, String clubName, Long userId, String userName) {
        MemberActivityDTO activity = MemberActivityDTO.builder()
            .activityType("LEFT")
            .clubId(clubId)
            .userId(userId)
            .userName(userName)
            .timestamp(LocalDateTime.now())
            .build();
        sendMemberActivity(clubId, activity);

        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("MEMBER_LEFT")
            .clubId(clubId)
            .clubName(clubName)
            .message(userName + " left the club")
            .data(activity)
            .timestamp(LocalDateTime.now())
            .build();
        sendClubNotification(clubId, notification);
    }

    /**
     * Helper method to create and send new membership request notification
     */
    public void notifyNewMembershipRequest(Long clubId, String clubName, Long userId, String userName) {
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("MEMBERSHIP_REQUEST")
            .clubId(clubId)
            .clubName(clubName)
            .message(userName + " wants to join the club")
            .timestamp(LocalDateTime.now())
            .build();
        sendClubNotification(clubId, notification);
    }
}
