package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GoogleMeetServiceTest {

    @Mock
    private GoogleOAuthService googleOAuthService;

    @InjectMocks
    private GoogleMeetService googleMeetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleMeetService, "enabled", false);
        ReflectionTestUtils.setField(googleMeetService, "useOAuth", true);
    }

    @Test
    void createMeetingWithDetails_WhenDisabled_ShouldReturnInstantMeetLink() {
        // Given
        String title = "Interview with John Doe";
        String description = "Technical interview";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        int durationMinutes = 60;

        // When
        GoogleMeetService.MeetingCreationResult result = googleMeetService.createMeetingWithDetails(
                title, description, startTime, durationMinutes
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMeetingLink()).isNotNull();
        assertThat(result.getMeetingLink()).contains("meet.google.com");
        assertThat(result.getMessage()).contains("not enabled");
    }

    @Test
    void createMeetingLink_WhenDisabled_ShouldReturnInstantMeetLink() {
        // Given
        String title = "Interview";
        String description = "Technical interview";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);

        // When
        String meetingLink = googleMeetService.createMeetingLink(title, description, startTime, 60);

        // Then
        assertThat(meetingLink).isNotNull();
        assertThat(meetingLink).contains("meet.google.com");
    }

    @Test
    void getCalendarEvents_WhenDisabled_ShouldReturnEmptyList() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(7);

        // When
        var events = googleMeetService.getCalendarEvents(startTime, endTime);

        // Then
        assertThat(events).isEmpty();
    }

    @Test
    void hasScheduleConflict_WhenDisabled_ShouldReturnFalse() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        // When
        boolean hasConflict = googleMeetService.hasScheduleConflict(startTime, endTime);

        // Then
        assertThat(hasConflict).isFalse();
    }
}
