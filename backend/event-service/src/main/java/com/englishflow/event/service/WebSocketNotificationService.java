package com.englishflow.event.service;

import com.englishflow.event.dto.EventNotificationDTO;
import com.englishflow.event.dto.ParticipantActivityDTO;
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
     * Send notification to all subscribers of a specific event
     */
    public void sendEventNotification(Long eventId, EventNotificationDTO notification) {
        log.info("Sending WebSocket notification to event {}: {}", eventId, notification.getType());
        messagingTemplate.convertAndSend("/topic/event/" + eventId, notification);
    }

    /**
     * Send notification to all events (broadcast)
     */
    public void sendGlobalEventNotification(EventNotificationDTO notification) {
        log.info("Sending global WebSocket notification: {}", notification.getType());
        messagingTemplate.convertAndSend("/topic/events", notification);
    }

    /**
     * Send participant activity notification
     */
    public void sendParticipantActivity(Long eventId, ParticipantActivityDTO activity) {
        log.info("Sending participant activity to event {}: {}", eventId, activity.getActivityType());
        messagingTemplate.convertAndSend("/topic/event/" + eventId + "/participants", activity);
    }

    /**
     * Send notification to a specific user
     */
    public void sendUserNotification(Long userId, EventNotificationDTO notification) {
        log.info("Sending WebSocket notification to user {}: {}", userId, notification.getType());
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }

    /**
     * Helper method to create and send event created notification
     */
    public void notifyEventCreated(Long eventId, String eventTitle) {
        EventNotificationDTO notification = EventNotificationDTO.builder()
            .type("EVENT_CREATED")
            .eventId(eventId)
            .eventTitle(eventTitle)
            .message("New event '" + eventTitle + "' has been created")
            .timestamp(LocalDateTime.now())
            .build();
        sendGlobalEventNotification(notification);
    }

    /**
     * Helper method to create and send event updated notification
     */
    public void notifyEventUpdated(Long eventId, String eventTitle) {
        EventNotificationDTO notification = EventNotificationDTO.builder()
            .type("EVENT_UPDATED")
            .eventId(eventId)
            .eventTitle(eventTitle)
            .message("Event '" + eventTitle + "' has been updated")
            .timestamp(LocalDateTime.now())
            .build();
        sendEventNotification(eventId, notification);
    }

    /**
     * Helper method to create and send participant joined notification
     */
    public void notifyParticipantJoined(Long eventId, String eventTitle, Long userId, String userName, 
                                       Integer currentParticipants, Integer maxParticipants) {
        ParticipantActivityDTO activity = ParticipantActivityDTO.builder()
            .activityType("JOINED")
            .eventId(eventId)
            .userId(userId)
            .userName(userName)
            .currentParticipants(currentParticipants)
            .maxParticipants(maxParticipants)
            .timestamp(LocalDateTime.now())
            .build();
        sendParticipantActivity(eventId, activity);

        EventNotificationDTO notification = EventNotificationDTO.builder()
            .type("PARTICIPANT_JOINED")
            .eventId(eventId)
            .eventTitle(eventTitle)
            .message(userName + " joined the event (" + currentParticipants + "/" + maxParticipants + ")")
            .data(activity)
            .timestamp(LocalDateTime.now())
            .build();
        sendEventNotification(eventId, notification);
    }

    /**
     * Helper method to create and send participant left notification
     */
    public void notifyParticipantLeft(Long eventId, String eventTitle, Long userId, String userName,
                                     Integer currentParticipants, Integer maxParticipants) {
        ParticipantActivityDTO activity = ParticipantActivityDTO.builder()
            .activityType("LEFT")
            .eventId(eventId)
            .userId(userId)
            .userName(userName)
            .currentParticipants(currentParticipants)
            .maxParticipants(maxParticipants)
            .timestamp(LocalDateTime.now())
            .build();
        sendParticipantActivity(eventId, activity);

        EventNotificationDTO notification = EventNotificationDTO.builder()
            .type("PARTICIPANT_LEFT")
            .eventId(eventId)
            .eventTitle(eventTitle)
            .message(userName + " left the event (" + currentParticipants + "/" + maxParticipants + ")")
            .data(activity)
            .timestamp(LocalDateTime.now())
            .build();
        sendEventNotification(eventId, notification);
    }

    /**
     * Helper method to create and send event cancelled notification
     */
    public void notifyEventCancelled(Long eventId, String eventTitle) {
        EventNotificationDTO notification = EventNotificationDTO.builder()
            .type("EVENT_CANCELLED")
            .eventId(eventId)
            .eventTitle(eventTitle)
            .message("Event '" + eventTitle + "' has been cancelled")
            .timestamp(LocalDateTime.now())
            .build();
        sendEventNotification(eventId, notification);
    }
}
