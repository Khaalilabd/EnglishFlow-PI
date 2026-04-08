package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.GenerateMeetingLinkRequest;
import com.englishflow.auth.dto.recruitment.MeetingLinkResponse;
import com.englishflow.auth.enums.MeetingPlatform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingLinkService {

    @Value("${meeting.google.enabled:false}")
    private boolean googleMeetEnabled;

    @Value("${meeting.zoom.enabled:false}")
    private boolean zoomEnabled;

    @Value("${meeting.teams.enabled:false}")
    private boolean teamsEnabled;

    private final GoogleMeetService googleMeetService;
    private final ZoomService zoomService;

    /**
     * Génère un lien de réunion selon la plateforme choisie
     */
    public MeetingLinkResponse generateMeetingLink(GenerateMeetingLinkRequest request) {
        log.info("Generating meeting link for platform: {}", request.getPlatform());

        switch (request.getPlatform()) {
            case GOOGLE_MEET:
                return generateGoogleMeetLink(request);
            case ZOOM:
                return generateZoomLink(request);
            case MICROSOFT_TEAMS:
                return generateTeamsLink(request);
            case MANUAL:
                throw new IllegalArgumentException("Manual platform requires explicit meeting link");
            default:
                throw new IllegalArgumentException("Unsupported meeting platform: " + request.getPlatform());
        }
    }

    /**
     * Génère un lien Google Meet
     */
    private MeetingLinkResponse generateGoogleMeetLink(GenerateMeetingLinkRequest request) {
        log.info("Generating Google Meet link");
        
        try {
            String title = request.getTitle() != null ? request.getTitle() : "Interview Meeting";
            String description = request.getDescription() != null ? request.getDescription() : "Recruitment interview";
            int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 60;
            
            String meetingLink = googleMeetService.createMeetingLink(
                title, 
                description, 
                request.getInterviewScheduledAt(), 
                duration
            );
            
            // Extraire le meeting ID du lien
            String meetingId = meetingLink.substring(meetingLink.lastIndexOf("/") + 1);
            
            String additionalInfo;
            if (googleMeetEnabled && !meetingLink.endsWith("/new")) {
                additionalInfo = "Lien Google Meet créé avec succès via Calendar API";
            } else {
                additionalInfo = "Lien Google Meet instantané - Cliquez pour créer une nouvelle réunion. Note: Nécessite Google Workspace pour l'intégration Calendar API complète.";
            }

            return MeetingLinkResponse.builder()
                    .meetingLink(meetingLink)
                    .platform(MeetingPlatform.GOOGLE_MEET)
                    .meetingId(meetingId)
                    .scheduledAt(request.getInterviewScheduledAt())
                    .durationMinutes(duration)
                    .additionalInfo(additionalInfo)
                    .build();
        } catch (Exception e) {
            log.error("Error generating Google Meet link", e);
            return generatePlaceholderLink(request, MeetingPlatform.GOOGLE_MEET);
        }
    }

    /**
     * Génère un lien Zoom
     */
    private MeetingLinkResponse generateZoomLink(GenerateMeetingLinkRequest request) {
        log.info("Generating Zoom link");
        
        try {
            String title = request.getTitle() != null ? request.getTitle() : "Interview Meeting";
            int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 60;
            
            Map<String, String> zoomMeeting = zoomService.createMeeting(
                title,
                request.getInterviewScheduledAt(),
                duration
            );
            
            String additionalInfo = zoomEnabled ? 
                "Zoom meeting created successfully" : 
                "Placeholder link (Configure Zoom API for real meetings)";

            return MeetingLinkResponse.builder()
                    .meetingLink(zoomMeeting.get("meetingLink"))
                    .platform(MeetingPlatform.ZOOM)
                    .meetingId(zoomMeeting.get("meetingId"))
                    .password(zoomMeeting.get("password"))
                    .scheduledAt(request.getInterviewScheduledAt())
                    .durationMinutes(duration)
                    .additionalInfo(additionalInfo)
                    .build();
        } catch (Exception e) {
            log.error("Error generating Zoom link", e);
            return generatePlaceholderLink(request, MeetingPlatform.ZOOM);
        }
    }

    /**
     * Génère un lien Microsoft Teams
     * TODO: Intégrer avec Microsoft Graph API pour créer des réunions réelles
     */
    private MeetingLinkResponse generateTeamsLink(GenerateMeetingLinkRequest request) {
        if (!teamsEnabled) {
            log.warn("Microsoft Teams integration not enabled, generating placeholder link");
            return generatePlaceholderLink(request, MeetingPlatform.MICROSOFT_TEAMS);
        }

        // TODO: Implémenter l'intégration réelle avec Microsoft Graph API
        String meetingId = UUID.randomUUID().toString();
        String meetingLink = "https://teams.microsoft.com/l/meetup-join/" + meetingId;

        return MeetingLinkResponse.builder()
                .meetingLink(meetingLink)
                .platform(MeetingPlatform.MICROSOFT_TEAMS)
                .meetingId(meetingId)
                .scheduledAt(request.getInterviewScheduledAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .additionalInfo("Teams link generated. Integration pending for calendar event creation.")
                .build();
    }

    /**
     * Génère un lien placeholder quand l'intégration n'est pas configurée
     */
    private MeetingLinkResponse generatePlaceholderLink(GenerateMeetingLinkRequest request, MeetingPlatform platform) {
        String meetingId = UUID.randomUUID().toString().substring(0, 10);
        
        return MeetingLinkResponse.builder()
                .meetingLink("https://placeholder.meeting/" + meetingId)
                .platform(platform)
                .meetingId(meetingId)
                .scheduledAt(request.getInterviewScheduledAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .additionalInfo("Placeholder link. Configure " + platform.getDisplayName() + " integration in application.properties")
                .build();
    }

    /**
     * Vérifie si une plateforme est disponible
     */
    public boolean isPlatformAvailable(MeetingPlatform platform) {
        switch (platform) {
            case GOOGLE_MEET:
                return googleMeetEnabled;
            case ZOOM:
                return zoomEnabled;
            case MICROSOFT_TEAMS:
                return teamsEnabled;
            case MANUAL:
                return true;
            default:
                return false;
        }
    }
}
