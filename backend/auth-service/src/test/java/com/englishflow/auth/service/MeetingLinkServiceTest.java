package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.GenerateMeetingLinkRequest;
import com.englishflow.auth.dto.recruitment.MeetingLinkResponse;
import com.englishflow.auth.enums.MeetingPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class MeetingLinkServiceTest {

    private MeetingLinkService meetingLinkService;
    
    @Mock
    private GoogleMeetService googleMeetService;
    
    @Mock
    private ZoomService zoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meetingLinkService = new MeetingLinkService(googleMeetService, zoomService);
        
        // Simuler la configuration
        ReflectionTestUtils.setField(meetingLinkService, "googleMeetEnabled", false);
        ReflectionTestUtils.setField(meetingLinkService, "zoomEnabled", false);
        ReflectionTestUtils.setField(meetingLinkService, "teamsEnabled", false);
    }

    @Test
    void testGenerateGoogleMeetLink() {
        // Mock du service Google Meet
        when(googleMeetService.createMeetingLink(anyString(), anyString(), any(LocalDateTime.class), anyInt()))
            .thenReturn("https://meet.google.com/abc-defg-hij");
        
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        request.setPlatform(MeetingPlatform.GOOGLE_MEET);
        request.setInterviewScheduledAt(LocalDateTime.now().plusDays(1));
        request.setTitle("Interview Test");
        request.setDurationMinutes(60);

        MeetingLinkResponse response = meetingLinkService.generateMeetingLink(request);

        assertNotNull(response);
        assertEquals(MeetingPlatform.GOOGLE_MEET, response.getPlatform());
        assertNotNull(response.getMeetingLink());
        assertTrue(response.getMeetingLink().contains("meet.google.com"));
        assertNotNull(response.getMeetingId());
        assertEquals(60, response.getDurationMinutes());
    }

    @Test
    void testGenerateZoomLink() {
        // Mock du service Zoom
        Map<String, String> zoomMeeting = new HashMap<>();
        zoomMeeting.put("meetingLink", "https://zoom.us/j/123456789?pwd=abc123");
        zoomMeeting.put("meetingId", "123456789");
        zoomMeeting.put("password", "abc123");
        
        when(zoomService.createMeeting(anyString(), any(LocalDateTime.class), anyInt()))
            .thenReturn(zoomMeeting);
        
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        request.setPlatform(MeetingPlatform.ZOOM);
        request.setInterviewScheduledAt(LocalDateTime.now().plusDays(1));
        request.setTitle("Interview Test");
        request.setDurationMinutes(45);

        MeetingLinkResponse response = meetingLinkService.generateMeetingLink(request);

        assertNotNull(response);
        assertEquals(MeetingPlatform.ZOOM, response.getPlatform());
        assertNotNull(response.getMeetingLink());
        assertTrue(response.getMeetingLink().contains("zoom.us"));
        assertNotNull(response.getMeetingId());
        assertNotNull(response.getPassword());
        assertEquals(45, response.getDurationMinutes());
    }

    @Test
    void testGenerateTeamsLink() {
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        request.setPlatform(MeetingPlatform.MICROSOFT_TEAMS);
        request.setInterviewScheduledAt(LocalDateTime.now().plusDays(1));
        request.setTitle("Interview Test");
        request.setDurationMinutes(30);

        MeetingLinkResponse response = meetingLinkService.generateMeetingLink(request);

        assertNotNull(response);
        assertEquals(MeetingPlatform.MICROSOFT_TEAMS, response.getPlatform());
        assertNotNull(response.getMeetingLink());
        // Teams génère un lien placeholder ou un vrai lien teams
        assertTrue(response.getMeetingLink().contains("teams.microsoft.com") || 
                   response.getMeetingLink().contains("placeholder.meeting"));
        assertNotNull(response.getMeetingId());
        assertEquals(30, response.getDurationMinutes());
    }

    @Test
    void testManualPlatformThrowsException() {
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        request.setPlatform(MeetingPlatform.MANUAL);
        request.setInterviewScheduledAt(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            meetingLinkService.generateMeetingLink(request);
        });
    }

    @Test
    void testDefaultDuration() {
        when(googleMeetService.createMeetingLink(anyString(), anyString(), any(LocalDateTime.class), anyInt()))
            .thenReturn("https://meet.google.com/test-link");
        
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        request.setPlatform(MeetingPlatform.GOOGLE_MEET);
        request.setInterviewScheduledAt(LocalDateTime.now().plusDays(1));
        request.setTitle("Interview Test");
        // Pas de durée spécifiée

        MeetingLinkResponse response = meetingLinkService.generateMeetingLink(request);

        assertEquals(60, response.getDurationMinutes()); // Durée par défaut
    }

    @Test
    void testIsPlatformAvailable() {
        assertTrue(meetingLinkService.isPlatformAvailable(MeetingPlatform.MANUAL));
        assertFalse(meetingLinkService.isPlatformAvailable(MeetingPlatform.GOOGLE_MEET));
        assertFalse(meetingLinkService.isPlatformAvailable(MeetingPlatform.ZOOM));
        assertFalse(meetingLinkService.isPlatformAvailable(MeetingPlatform.MICROSOFT_TEAMS));
    }
}
