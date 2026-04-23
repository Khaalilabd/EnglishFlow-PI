package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ZoomServiceTest {

    private ZoomService zoomService;

    @BeforeEach
    void setUp() {
        zoomService = new ZoomService();
        ReflectionTestUtils.setField(zoomService, "enabled", false);
    }

    @Test
    void createMeeting_WhenDisabled_ShouldReturnPlaceholder() {
        // Given
        String title = "Interview with John Doe";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        int durationMinutes = 60;

        // When
        Map<String, String> result = zoomService.createMeeting(title, startTime, durationMinutes);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).containsKeys("meetingLink", "meetingId", "password");
        assertThat(result.get("meetingLink")).contains("zoom.us");
    }

    @Test
    void createMeeting_WhenNotConfigured_ShouldReturnPlaceholder() {
        // Given
        ReflectionTestUtils.setField(zoomService, "enabled", true);
        ReflectionTestUtils.setField(zoomService, "clientId", null);
        ReflectionTestUtils.setField(zoomService, "clientSecret", null);
        
        String title = "Interview";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);

        // When
        Map<String, String> result = zoomService.createMeeting(title, startTime, 60);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("meetingLink")).isNotNull();
        assertThat(result.get("meetingId")).isNotNull();
    }
}
