package com.englishflow.club.service;

import com.englishflow.club.dto.ClubNotificationDTO;
import com.englishflow.club.dto.MemberActivityDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationService webSocketNotificationService;

    @Test
    void sendClubNotification_ShouldSendToCorrectTopic() {
        // Given
        Long clubId = 1L;
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("TEST_NOTIFICATION")
            .clubId(clubId)
            .clubName("Test Club")
            .message("Test message")
            .build();

        // When
        webSocketNotificationService.sendClubNotification(clubId, notification);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/club/1", notification);
    }

    @Test
    void sendGlobalClubNotification_ShouldSendToGlobalTopic() {
        // Given
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("GLOBAL_NOTIFICATION")
            .message("Global message")
            .build();

        // When
        webSocketNotificationService.sendGlobalClubNotification(notification);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/clubs", notification);
    }

    @Test
    void sendMemberActivity_ShouldSendToMembersTopic() {
        // Given
        Long clubId = 2L;
        MemberActivityDTO activity = MemberActivityDTO.builder()
            .activityType("JOINED")
            .clubId(clubId)
            .userId(10L)
            .userName("John Doe")
            .role("MEMBER")
            .build();

        // When
        webSocketNotificationService.sendMemberActivity(clubId, activity);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/club/2/members", activity);
    }

    @Test
    void sendUserNotification_ShouldSendToUserQueue() {
        // Given
        Long userId = 5L;
        ClubNotificationDTO notification = ClubNotificationDTO.builder()
            .type("USER_NOTIFICATION")
            .message("Personal message")
            .build();

        // When
        webSocketNotificationService.sendUserNotification(userId, notification);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
            "5",
            "/queue/notifications",
            notification
        );
    }

    @Test
    void notifyClubCreated_ShouldCreateAndSendNotification() {
        // Given
        Long clubId = 3L;
        String clubName = "New Club";

        // When
        webSocketNotificationService.notifyClubCreated(clubId, clubName);

        // Then
        ArgumentCaptor<ClubNotificationDTO> captor = ArgumentCaptor.forClass(ClubNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/clubs"), captor.capture());

        ClubNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("CLUB_CREATED");
        assertThat(notification.getClubId()).isEqualTo(clubId);
        assertThat(notification.getClubName()).isEqualTo(clubName);
        assertThat(notification.getMessage()).contains("New club 'New Club' has been created");
        assertThat(notification.getTimestamp()).isNotNull();
    }

    @Test
    void notifyClubUpdated_ShouldCreateAndSendNotification() {
        // Given
        Long clubId = 4L;
        String clubName = "Updated Club";

        // When
        webSocketNotificationService.notifyClubUpdated(clubId, clubName);

        // Then
        ArgumentCaptor<ClubNotificationDTO> captor = ArgumentCaptor.forClass(ClubNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/4"), captor.capture());

        ClubNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("CLUB_UPDATED");
        assertThat(notification.getClubId()).isEqualTo(clubId);
        assertThat(notification.getClubName()).isEqualTo(clubName);
        assertThat(notification.getMessage()).contains("Club 'Updated Club' has been updated");
        assertThat(notification.getTimestamp()).isNotNull();
    }

    @Test
    void notifyMemberJoined_ShouldSendActivityAndNotification() {
        // Given
        Long clubId = 5L;
        String clubName = "Test Club";
        Long userId = 10L;
        String userName = "Jane Smith";
        String role = "MEMBER";

        // When
        webSocketNotificationService.notifyMemberJoined(clubId, clubName, userId, userName, role);

        // Then
        // Verify member activity was sent
        ArgumentCaptor<MemberActivityDTO> activityCaptor = ArgumentCaptor.forClass(MemberActivityDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/5/members"), activityCaptor.capture());

        MemberActivityDTO activity = activityCaptor.getValue();
        assertThat(activity.getActivityType()).isEqualTo("JOINED");
        assertThat(activity.getClubId()).isEqualTo(clubId);
        assertThat(activity.getUserId()).isEqualTo(userId);
        assertThat(activity.getUserName()).isEqualTo(userName);
        assertThat(activity.getRole()).isEqualTo(role);
        assertThat(activity.getTimestamp()).isNotNull();

        // Verify club notification was sent
        ArgumentCaptor<ClubNotificationDTO> notificationCaptor = ArgumentCaptor.forClass(ClubNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/5"), notificationCaptor.capture());

        ClubNotificationDTO notification = notificationCaptor.getValue();
        assertThat(notification.getType()).isEqualTo("MEMBER_JOINED");
        assertThat(notification.getClubId()).isEqualTo(clubId);
        assertThat(notification.getClubName()).isEqualTo(clubName);
        assertThat(notification.getMessage()).contains("Jane Smith joined the club");
        assertThat(notification.getData()).isNotNull();
        assertThat(notification.getTimestamp()).isNotNull();
    }

    @Test
    void notifyMemberLeft_ShouldSendActivityAndNotification() {
        // Given
        Long clubId = 6L;
        String clubName = "Test Club";
        Long userId = 11L;
        String userName = "Bob Johnson";

        // When
        webSocketNotificationService.notifyMemberLeft(clubId, clubName, userId, userName);

        // Then
        // Verify member activity was sent
        ArgumentCaptor<MemberActivityDTO> activityCaptor = ArgumentCaptor.forClass(MemberActivityDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/6/members"), activityCaptor.capture());

        MemberActivityDTO activity = activityCaptor.getValue();
        assertThat(activity.getActivityType()).isEqualTo("LEFT");
        assertThat(activity.getClubId()).isEqualTo(clubId);
        assertThat(activity.getUserId()).isEqualTo(userId);
        assertThat(activity.getUserName()).isEqualTo(userName);
        assertThat(activity.getTimestamp()).isNotNull();

        // Verify club notification was sent
        ArgumentCaptor<ClubNotificationDTO> notificationCaptor = ArgumentCaptor.forClass(ClubNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/6"), notificationCaptor.capture());

        ClubNotificationDTO notification = notificationCaptor.getValue();
        assertThat(notification.getType()).isEqualTo("MEMBER_LEFT");
        assertThat(notification.getClubId()).isEqualTo(clubId);
        assertThat(notification.getClubName()).isEqualTo(clubName);
        assertThat(notification.getMessage()).contains("Bob Johnson left the club");
        assertThat(notification.getData()).isNotNull();
        assertThat(notification.getTimestamp()).isNotNull();
    }

    @Test
    void notifyNewMembershipRequest_ShouldCreateAndSendNotification() {
        // Given
        Long clubId = 7L;
        String clubName = "Test Club";
        Long userId = 12L;
        String userName = "Alice Brown";

        // When
        webSocketNotificationService.notifyNewMembershipRequest(clubId, clubName, userId, userName);

        // Then
        ArgumentCaptor<ClubNotificationDTO> captor = ArgumentCaptor.forClass(ClubNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/club/7"), captor.capture());

        ClubNotificationDTO notification = captor.getValue();
        assertThat(notification.getType()).isEqualTo("MEMBERSHIP_REQUEST");
        assertThat(notification.getClubId()).isEqualTo(clubId);
        assertThat(notification.getClubName()).isEqualTo(clubName);
        assertThat(notification.getMessage()).contains("Alice Brown wants to join the club");
        assertThat(notification.getTimestamp()).isNotNull();
    }

    @Test
    void sendClubNotification_WithDifferentClubIds_ShouldSendToCorrectTopics() {
        // Given
        ClubNotificationDTO notification1 = ClubNotificationDTO.builder()
            .type("TEST")
            .clubId(100L)
            .build();

        ClubNotificationDTO notification2 = ClubNotificationDTO.builder()
            .type("TEST")
            .clubId(200L)
            .build();

        // When
        webSocketNotificationService.sendClubNotification(100L, notification1);
        webSocketNotificationService.sendClubNotification(200L, notification2);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/club/100", notification1);
        verify(messagingTemplate).convertAndSend("/topic/club/200", notification2);
    }

    @Test
    void sendUserNotification_WithDifferentUserIds_ShouldSendToCorrectQueues() {
        // Given
        ClubNotificationDTO notification1 = ClubNotificationDTO.builder()
            .type("TEST")
            .build();

        ClubNotificationDTO notification2 = ClubNotificationDTO.builder()
            .type("TEST")
            .build();

        // When
        webSocketNotificationService.sendUserNotification(50L, notification1);
        webSocketNotificationService.sendUserNotification(60L, notification2);

        // Then
        verify(messagingTemplate).convertAndSendToUser("50", "/queue/notifications", notification1);
        verify(messagingTemplate).convertAndSendToUser("60", "/queue/notifications", notification2);
    }
}
