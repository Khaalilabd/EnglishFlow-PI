package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.CalendarAvailabilityRequest;
import com.englishflow.auth.dto.recruitment.CalendarAvailabilityResponse;
import com.englishflow.auth.entity.InterviewSchedule;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.InterviewScheduleRepository;
import com.englishflow.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewScheduleServiceTest {

    @Mock
    private InterviewScheduleRepository scheduleRepository;

    @Mock
    private GoogleMeetService googleMeetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InterviewScheduleService interviewScheduleService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
    }

    @Test
    void testCalendarAvailabilityRequest() {
        // Test de création d'une requête de disponibilité
        CalendarAvailabilityRequest request = new CalendarAvailabilityRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        
        assertNotNull(request);
        assertEquals(LocalDate.now(), request.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), request.getEndDate());
    }

    @Test
    void testGetCalendarAvailabilityWithNoEvents() {
        // Arrange
        CalendarAvailabilityRequest request = new CalendarAvailabilityRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setInterviewerId(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(scheduleRepository.findScheduledInterviewsByInterviewerAndDateRange(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(googleMeetService.getCalendarEvents(any(), any())).thenReturn(new ArrayList<>());

        // Act
        CalendarAvailabilityResponse response = interviewScheduleService.getCalendarAvailability(request, 1L);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getScheduledEvents(), "Scheduled events list should not be null");
        assertEquals(0, response.getScheduledEvents().size(), "Should have no events");
        assertEquals("John Doe", response.getInterviewerName(), "Interviewer name should match");
        assertFalse(response.isHasConflicts(), "Should have no conflicts");
    }

    @Test
    void testGetCalendarAvailabilityWithEvents() {
        // Arrange
        CalendarAvailabilityRequest request = new CalendarAvailabilityRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setInterviewerId(1L);

        // Create mock TutorApplication
        com.englishflow.auth.entity.TutorApplication mockApplication = new com.englishflow.auth.entity.TutorApplication();
        mockApplication.setId(1L);
        mockApplication.setFirstName("Jane");
        mockApplication.setLastName("Smith");
        mockApplication.setEmail("jane.smith@example.com");

        List<InterviewSchedule> mockSchedules = new ArrayList<>();
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setId(1L);
        schedule.setScheduledStart(LocalDateTime.now().plusDays(1));
        schedule.setScheduledEnd(LocalDateTime.now().plusDays(1).plusHours(1));
        schedule.setTitle("Test Interview");
        schedule.setApplication(mockApplication); // Set the application
        schedule.setStatus(InterviewSchedule.ScheduleStatus.SCHEDULED);
        mockSchedules.add(schedule);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(scheduleRepository.findScheduledInterviewsByInterviewerAndDateRange(anyLong(), any(), any()))
                .thenReturn(mockSchedules);
        when(googleMeetService.getCalendarEvents(any(), any())).thenReturn(new ArrayList<>());

        // Act
        CalendarAvailabilityResponse response = interviewScheduleService.getCalendarAvailability(request, 1L);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getScheduledEvents(), "Scheduled events list should not be null");
        assertEquals(1, response.getScheduledEvents().size(), "Should have one event");
        assertEquals("John Doe", response.getInterviewerName(), "Interviewer name should match");
    }

    @Test
    void testGetUpcomingInterviews() {
        // Arrange
        List<InterviewSchedule> mockSchedules = new ArrayList<>();
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setId(1L);
        schedule.setScheduledStart(LocalDateTime.now().plusDays(1));
        schedule.setScheduledEnd(LocalDateTime.now().plusDays(1).plusHours(1));
        mockSchedules.add(schedule);

        when(scheduleRepository.findUpcomingInterviewsByInterviewer(anyLong(), any()))
                .thenReturn(mockSchedules);

        // Act
        List<InterviewSchedule> result = interviewScheduleService.getUpcomingInterviews(1L);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should have one upcoming interview");
    }
}
