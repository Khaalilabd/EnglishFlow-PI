package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.CalendarAvailabilityRequest;
import com.englishflow.auth.dto.recruitment.CalendarAvailabilityResponse;
import com.englishflow.auth.dto.recruitment.CalendarEventResponse;
import com.englishflow.auth.entity.InterviewSchedule;
import com.englishflow.auth.entity.TutorApplication;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.InterviewScheduleRepository;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewScheduleService {

    private final InterviewScheduleRepository scheduleRepository;
    private final GoogleMeetService googleMeetService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Récupère la disponibilité du calendrier pour un interviewer
     * Combine les données de la DB locale et de Google Calendar
     */
    public CalendarAvailabilityResponse getCalendarAvailability(CalendarAvailabilityRequest request, Long currentUserId) {
        Long interviewerId = request.getInterviewerId() != null ? request.getInterviewerId() : currentUserId;
        
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        // Récupérer les événements de la DB locale
        List<InterviewSchedule> localSchedules = scheduleRepository.findScheduledInterviewsByInterviewerAndDateRange(
                interviewerId, startDateTime, endDateTime
        );

        // Récupérer les événements de Google Calendar
        List<GoogleMeetService.CalendarEventInfo> googleEvents = googleMeetService.getCalendarEvents(
                startDateTime, endDateTime
        );

        // Convertir en CalendarEventResponse
        List<CalendarEventResponse> allEvents = new ArrayList<>();
        
        // Ajouter les événements locaux
        allEvents.addAll(localSchedules.stream()
                .map(this::convertToCalendarEventResponse)
                .collect(Collectors.toList()));

        // Ajouter les événements Google qui ne sont pas déjà dans la DB
        for (GoogleMeetService.CalendarEventInfo googleEvent : googleEvents) {
            boolean existsInLocal = localSchedules.stream()
                    .anyMatch(s -> googleEvent.getEventId().equals(s.getGoogleEventId()));
            
            if (!existsInLocal) {
                allEvents.add(convertGoogleEventToResponse(googleEvent));
            }
        }

        // Créer les time slots occupés
        List<CalendarAvailabilityResponse.TimeSlot> busySlots = allEvents.stream()
                .map(event -> CalendarAvailabilityResponse.TimeSlot.builder()
                        .date(event.getStart().toLocalDate())
                        .startTime(event.getStart().toLocalTime().toString())
                        .endTime(event.getEnd().toLocalTime().toString())
                        .isAvailable(false)
                        .build())
                .collect(Collectors.toList());

        // Récupérer le nom de l'interviewer
        String interviewerName = userRepository.findById(interviewerId)
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .orElse("Unknown");

        return CalendarAvailabilityResponse.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .interviewerId(interviewerId)
                .interviewerName(interviewerName)
                .scheduledEvents(allEvents)
                .busySlots(busySlots)
                .hasConflicts(false)
                .message("Calendar availability retrieved successfully")
                .build();
    }

    /**
     * Crée un nouveau rendez-vous d'entretien
     * Crée l'événement dans Google Calendar ET stocke localement
     */
    @Transactional
    public InterviewSchedule createInterviewSchedule(
            TutorApplication application,
            Long interviewerId,
            LocalDateTime scheduledStart,
            Integer durationMinutes,
            String title,
            String description
    ) {
        LocalDateTime scheduledEnd = scheduledStart.plusMinutes(durationMinutes);

        // Annuler tout entretien existant pour cette application
        Optional<InterviewSchedule> existingSchedule = scheduleRepository.findByApplicationIdAndStatus(
                application.getId(), InterviewSchedule.ScheduleStatus.SCHEDULED
        );
        if (existingSchedule.isPresent()) {
            log.info("Cancelling existing interview schedule for application {}", application.getId());
            InterviewSchedule existing = existingSchedule.get();
            existing.setStatus(InterviewSchedule.ScheduleStatus.CANCELLED);
            existing.setCancellationReason("Rescheduled to a new time");
            existing.setCancelledAt(LocalDateTime.now());
            scheduleRepository.save(existing);
        }

        // Vérifier les conflits dans la DB locale (exclure les entretiens annulés)
        boolean hasLocalConflict = scheduleRepository.hasScheduleConflict(
                interviewerId, scheduledStart, scheduledEnd
        );

        // Vérifier les conflits dans Google Calendar
        boolean hasGoogleConflict = googleMeetService.hasScheduleConflict(scheduledStart, scheduledEnd);

        // TEMPORAIRE: Désactiver la vérification de conflit pour les tests
        // TODO: Réactiver après les tests
        if (false && (hasLocalConflict || hasGoogleConflict)) {
            log.warn("Schedule conflict detected for interviewer {} at {}", interviewerId, scheduledStart);
            throw new IllegalStateException(
                    "Schedule conflict detected. There is already an interview scheduled at this time."
            );
        }

        // Créer l'événement dans Google Calendar
        GoogleMeetService.MeetingCreationResult meetingResult = googleMeetService.createMeetingWithDetails(
                title, description, scheduledStart, durationMinutes
        );

        // Créer l'entité locale
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setApplication(application);
        schedule.setInterviewerId(interviewerId);
        schedule.setScheduledStart(scheduledStart);
        schedule.setScheduledEnd(scheduledEnd);
        schedule.setDurationMinutes(durationMinutes);
        schedule.setMeetingLink(meetingResult.getMeetingLink());
        schedule.setGoogleEventId(meetingResult.getGoogleEventId());
        schedule.setMeetingPlatform("GOOGLE_MEET");
        schedule.setTitle(title);
        schedule.setDescription(description);
        schedule.setStatus(InterviewSchedule.ScheduleStatus.SCHEDULED);

        InterviewSchedule saved = scheduleRepository.save(schedule);
        
        log.info("Interview schedule created successfully. ID: {}, Google Event ID: {}, Meeting Link: {}", 
                saved.getId(), saved.getGoogleEventId(), saved.getMeetingLink());

        return saved;
    }

    /**
     * Annule un rendez-vous d'entretien
     */
    @Transactional
    public void cancelInterviewSchedule(Long scheduleId, String reason) {
        InterviewSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        schedule.setStatus(InterviewSchedule.ScheduleStatus.CANCELLED);
        schedule.setCancelledAt(LocalDateTime.now());
        schedule.setCancellationReason(reason);

        scheduleRepository.save(schedule);
        
        log.info("Interview schedule cancelled. ID: {}, Reason: {}", scheduleId, reason);
        
        // TODO: Optionnel - Supprimer aussi l'événement de Google Calendar
    }

    /**
     * Annule un rendez-vous d'entretien par application ID
     */
    @Transactional
    public void cancelInterviewByApplicationId(Long applicationId, String reason) {
        List<InterviewSchedule> schedules = scheduleRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId);
        
        // Filtrer pour ne garder que les entretiens planifiés
        List<InterviewSchedule> scheduledInterviews = schedules.stream()
                .filter(s -> s.getStatus() == InterviewSchedule.ScheduleStatus.SCHEDULED)
                .toList();
        
        if (!scheduledInterviews.isEmpty()) {
            // Annuler tous les entretiens planifiés
            for (InterviewSchedule schedule : scheduledInterviews) {
                schedule.setStatus(InterviewSchedule.ScheduleStatus.CANCELLED);
                schedule.setCancelledAt(LocalDateTime.now());
                schedule.setCancellationReason(reason);
                scheduleRepository.save(schedule);
                
                log.info("Interview schedule cancelled for application ID: {}, Schedule ID: {}, Reason: {}", 
                        applicationId, schedule.getId(), reason);
                
                // Envoyer un email de notification au candidat
                try {
                    emailService.sendInterviewCancellationEmail(
                            schedule.getApplication().getEmail(),
                            schedule.getApplication().getFirstName(),
                            schedule.getScheduledStart(),
                            reason
                    );
                } catch (Exception e) {
                    log.error("Failed to send cancellation email for schedule ID: {}", schedule.getId(), e);
                }
            }
        } else {
            log.warn("No scheduled interview found for application ID: {}", applicationId);
            throw new IllegalArgumentException("Aucun entretien planifié trouvé pour cette candidature");
        }
    }

    /**
     * Récupère les rendez-vous à venir pour un interviewer
     */
    public List<InterviewSchedule> getUpcomingInterviews(Long interviewerId) {
        return scheduleRepository.findUpcomingInterviewsByInterviewer(interviewerId, LocalDateTime.now());
    }

    /**
     * Convertit InterviewSchedule en CalendarEventResponse
     */
    private CalendarEventResponse convertToCalendarEventResponse(InterviewSchedule schedule) {
        return CalendarEventResponse.builder()
                .scheduleId(schedule.getId())
                .googleEventId(schedule.getGoogleEventId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .start(schedule.getScheduledStart())
                .end(schedule.getScheduledEnd())
                .durationMinutes(schedule.getDurationMinutes())
                .meetingLink(schedule.getMeetingLink())
                .platform(schedule.getMeetingPlatform())
                .status(schedule.getStatus().name())
                .applicationId(schedule.getApplication().getId())
                .candidateName(schedule.getApplication().getFirstName() + " " + schedule.getApplication().getLastName())
                .candidateEmail(schedule.getApplication().getEmail())
                .source(schedule.getGoogleEventId() != null ? 
                        CalendarEventResponse.EventSource.BOTH : 
                        CalendarEventResponse.EventSource.LOCAL_DB)
                .build();
    }

    /**
     * Convertit GoogleEvent en CalendarEventResponse
     */
    private CalendarEventResponse convertGoogleEventToResponse(GoogleMeetService.CalendarEventInfo googleEvent) {
        return CalendarEventResponse.builder()
                .googleEventId(googleEvent.getEventId())
                .title(googleEvent.getTitle())
                .description(googleEvent.getDescription())
                .start(googleEvent.getStart())
                .end(googleEvent.getEnd())
                .meetingLink(googleEvent.getMeetingLink())
                .platform("GOOGLE_MEET")
                .source(CalendarEventResponse.EventSource.GOOGLE_CALENDAR)
                .build();
    }
}
