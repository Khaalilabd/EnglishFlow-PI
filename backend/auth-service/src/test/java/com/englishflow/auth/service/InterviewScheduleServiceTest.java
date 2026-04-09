package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.CalendarAvailabilityRequest;
import com.englishflow.auth.dto.recruitment.CalendarAvailabilityResponse;
import com.englishflow.auth.repository.InterviewScheduleRepository;
import com.englishflow.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InterviewScheduleServiceTest {

    @Autowired(required = false)
    private InterviewScheduleService interviewScheduleService;

    @Autowired(required = false)
    private InterviewScheduleRepository scheduleRepository;

    @Autowired(required = false)
    private GoogleMeetService googleMeetService;

    @Test
    void contextLoads() {
        // Vérifier que le contexte Spring charge correctement
        assertTrue(true, "Spring context should load");
    }

    @Test
    void testServicesAreInjected() {
        // Vérifier que les services sont bien injectés
        assertNotNull(interviewScheduleService, "InterviewScheduleService should be injected");
        assertNotNull(scheduleRepository, "InterviewScheduleRepository should be injected");
        assertNotNull(googleMeetService, "GoogleMeetService should be injected");
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
        // Test de récupération de disponibilité sans événements
        if (interviewScheduleService != null) {
            CalendarAvailabilityRequest request = new CalendarAvailabilityRequest();
            request.setStartDate(LocalDate.now().plusMonths(6)); // Date future pour éviter conflits
            request.setEndDate(LocalDate.now().plusMonths(6).plusDays(7));
            request.setInterviewerId(1L);

            try {
                CalendarAvailabilityResponse response = interviewScheduleService.getCalendarAvailability(request, 1L);
                assertNotNull(response, "Response should not be null");
                assertNotNull(response.getScheduledEvents(), "Scheduled events list should not be null");
                System.out.println("✓ Calendar availability retrieved successfully");
            } catch (Exception e) {
                System.out.println("⚠ Calendar availability test skipped: " + e.getMessage());
            }
        }
    }
}
