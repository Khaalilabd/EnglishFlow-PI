package com.englishflow.auth.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "EnglishFlow Recruitment";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleOAuthService googleOAuthService;

    @Value("${meeting.google.credentials-file:#{null}}")
    private String credentialsFilePath;

    @Value("${meeting.google.enabled:false}")
    private boolean enabled;
    
    @Value("${meeting.google.use-oauth:true}")
    private boolean useOAuth;

    /**
     * Crée un événement Google Calendar avec un lien Google Meet et retourne les détails complets
     */
    public MeetingCreationResult createMeetingWithDetails(String title, String description, LocalDateTime startTime, int durationMinutes) {
        if (!enabled) {
            log.warn("Google Meet integration not enabled, generating instant meet link");
            return MeetingCreationResult.builder()
                    .meetingLink(generateInstantMeetLinkWithParams(title))
                    .success(false)
                    .message("Google Calendar integration not enabled. Using instant meet link.")
                    .build();
        }

        try {
            Calendar service;
            
            // Utiliser OAuth2 si configuré, sinon service account
            if (useOAuth) {
                log.info("🔐 Using OAuth2 for Google Calendar API");
                service = getCalendarServiceWithOAuth();
            } else {
                log.info("🔑 Using Service Account for Google Calendar API");
                service = getCalendarServiceWithServiceAccount();
            }
            
            Event event = new Event()
                    .setSummary(title)
                    .setDescription(description);

            // Définir l'heure de début
            DateTime startDateTime = new DateTime(
                    java.util.Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant())
            );
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Africa/Tunis");
            event.setStart(start);

            // Définir l'heure de fin
            LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
            DateTime endDateTime = new DateTime(
                    java.util.Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())
            );
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Africa/Tunis");
            event.setEnd(end);

            // Ajouter la configuration Google Meet (fonctionne avec OAuth2)
            ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey()
                    .setType("hangoutsMeet");
            CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(conferenceSolutionKey);
            ConferenceData conferenceData = new ConferenceData()
                    .setCreateRequest(createConferenceRequest);
            event.setConferenceData(conferenceData);

            // Créer l'événement
            String calendarId = "primary";
            event = service.events().insert(calendarId, event)
                    .setConferenceDataVersion(1)
                    .execute();

            // Récupérer le lien Google Meet
            if (event.getConferenceData() != null && 
                event.getConferenceData().getEntryPoints() != null &&
                !event.getConferenceData().getEntryPoints().isEmpty()) {
                
                String meetLink = event.getConferenceData().getEntryPoints().get(0).getUri();
                log.info("✅ Google Meet link created successfully: {}", meetLink);
                log.info("📅 Event ID: {}", event.getId());
                log.info("🔗 Calendar Link: {}", event.getHtmlLink());
                
                return MeetingCreationResult.builder()
                        .meetingLink(meetLink)
                        .googleEventId(event.getId())
                        .eventHtmlLink(event.getHtmlLink())
                        .success(true)
                        .message("Meeting created successfully with Google Calendar and Meet")
                        .build();
            } else {
                log.warn("⚠️ Event created but no conference data found");
                log.info("📅 Event ID: {}", event.getId());
                
                // Fallback: créer l'événement dans le calendrier sans Meet
                return MeetingCreationResult.builder()
                        .meetingLink(generateInstantMeetLinkWithParams(title))
                        .googleEventId(event.getId())
                        .eventHtmlLink(event.getHtmlLink())
                        .success(false)
                        .message("Event created in calendar but no Meet link generated. Using instant link.")
                        .build();
            }

        } catch (Exception e) {
            log.error("❌ Failed to create Google Calendar event", e);
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            
            // Messages d'erreur spécifiques
            if (e.getMessage() != null) {
                if (e.getMessage().contains("403")) {
                    log.error("🔒 Permission denied. Please authorize the application.");
                } else if (e.getMessage().contains("Invalid conference type")) {
                    log.error("⚠️ Service account cannot create Meet conferences. Enable OAuth2 in configuration.");
                    log.error("💡 Set meeting.google.use-oauth=true in application.properties");
                }
            }
            
            return MeetingCreationResult.builder()
                    .meetingLink(generateInstantMeetLinkWithParams(title))
                    .success(false)
                    .message("Failed to create event: " + e.getMessage() + ". Using instant meet link.")
                    .build();
        }
    }

    /**
     * Crée un événement Google Calendar avec un lien Google Meet (méthode legacy)
     * Note: Nécessite Google Workspace pour fonctionner correctement
     */
    public String createMeetingLink(String title, String description, LocalDateTime startTime, int durationMinutes) {
        MeetingCreationResult result = createMeetingWithDetails(title, description, startTime, durationMinutes);
        return result.getMeetingLink();
    }

    /**
     * Récupère les événements du calendrier dans une période donnée
     */
    public List<CalendarEventInfo> getCalendarEvents(LocalDateTime startTime, LocalDateTime endTime) {
        if (!enabled) {
            log.warn("Google Calendar integration not enabled");
            return Collections.emptyList();
        }

        try {
            Calendar service;
            if (useOAuth) {
                service = getCalendarServiceWithOAuth();
            } else {
                service = getCalendarServiceWithServiceAccount();
            }
            
            DateTime timeMin = new DateTime(
                    java.util.Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant())
            );
            DateTime timeMax = new DateTime(
                    java.util.Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())
            );

            Events events = service.events().list("primary")
                    .setTimeMin(timeMin)
                    .setTimeMax(timeMax)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            if (items == null || items.isEmpty()) {
                log.info("No events found in the specified time range");
                return Collections.emptyList();
            }

            return items.stream()
                    .map(this::convertToCalendarEventInfo)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to retrieve calendar events: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Vérifie s'il y a un conflit d'horaire dans Google Calendar
     */
    public boolean hasScheduleConflict(LocalDateTime startTime, LocalDateTime endTime) {
        List<CalendarEventInfo> events = getCalendarEvents(startTime, endTime);
        return !events.isEmpty();
    }

    /**
     * Convertit un Event Google en CalendarEventInfo
     */
    private CalendarEventInfo convertToCalendarEventInfo(Event event) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (event.getStart() != null && event.getStart().getDateTime() != null) {
            start = LocalDateTime.ofInstant(
                    new java.util.Date(event.getStart().getDateTime().getValue()).toInstant(),
                    ZoneId.systemDefault()
            );
        }

        if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
            end = LocalDateTime.ofInstant(
                    new java.util.Date(event.getEnd().getDateTime().getValue()).toInstant(),
                    ZoneId.systemDefault()
            );
        }

        String meetLink = null;
        if (event.getConferenceData() != null && 
            event.getConferenceData().getEntryPoints() != null &&
            !event.getConferenceData().getEntryPoints().isEmpty()) {
            meetLink = event.getConferenceData().getEntryPoints().get(0).getUri();
        }

        return CalendarEventInfo.builder()
                .eventId(event.getId())
                .title(event.getSummary())
                .description(event.getDescription())
                .start(start)
                .end(end)
                .meetingLink(meetLink)
                .htmlLink(event.getHtmlLink())
                .build();
    }

    /**
     * Génère un lien Google Meet instantané qui fonctionne avec tous les comptes Google
     * Utilise le lien "new" qui crée automatiquement une nouvelle réunion
     * L'utilisateur doit être connecté à son compte Google pour créer la réunion
     */
    private String generateInstantMeetLink() {
        // Utiliser le lien "new" qui redirige vers une nouvelle réunion Google Meet
        // Ce lien fonctionne pour tous les comptes Google (gratuits et Workspace)
        String meetLink = "https://meet.google.com/new";
        log.info("Generated instant Google Meet link (new): {}", meetLink);
        return meetLink;
    }

    private Calendar getCalendarServiceWithOAuth() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        // Obtenir les credentials OAuth2
        Credential credential = googleOAuthService.getCredentials(HTTP_TRANSPORT);
        
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Calendar getCalendarServiceWithServiceAccount() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        GoogleCredentials credentials;
        if (credentialsFilePath != null && !credentialsFilePath.isEmpty()) {
            credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))
                    .createScoped(java.util.Arrays.asList(
                        "https://www.googleapis.com/auth/calendar",
                        "https://www.googleapis.com/auth/calendar.events"
                    ));
        } else {
            // Utiliser les credentials par défaut de l'application
            credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(java.util.Arrays.asList(
                        "https://www.googleapis.com/auth/calendar",
                        "https://www.googleapis.com/auth/calendar.events"
                    ));
        }

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private String generatePlaceholderLink() {
        String meetingCode = generateMeetingCode();
        String meetLink = "https://meet.google.com/" + meetingCode;
        log.info("📝 Generated placeholder Google Meet link: {}", meetLink);
        log.warn("⚠️ This is a placeholder link. To use real Google Calendar integration:");
        log.warn("   1. Share your Google Calendar with: englishflow-calendar-service@englishflow-recruitment.iam.gserviceaccount.com");
        log.warn("   2. Or configure Domain-Wide Delegation for Google Workspace");
        
        return meetLink;
    }
    
    private String generateMeetingCode() {
        // Générer un code de meeting réaliste (format: xxx-xxxx-xxx)
        String part1 = generateRandomCode(3);
        String part2 = generateRandomCode(4);
        String part3 = generateRandomCode(3);
        return part1 + "-" + part2 + "-" + part3;
    }
    
    private String generateRandomCode(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    /**
     * Classe pour retourner les détails complets d'une création de meeting
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeetingCreationResult {
        private String meetingLink;
        private String googleEventId;
        private String eventHtmlLink;
        private boolean success;
        private String message;
    }

    /**
     * Classe pour représenter un événement du calendrier
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarEventInfo {
        private String eventId;
        private String title;
        private String description;
        private LocalDateTime start;
        private LocalDateTime end;
        private String meetingLink;
        private String htmlLink;
    }
    
    /**
     * Génère un lien Google Meet instant avec le titre dans l'URL
     */
    private String generateInstantMeetLinkWithParams(String title) {
        // Google Meet ne supporte pas les paramètres dans /new, donc on utilise juste /new
        // L'utilisateur devra créer la réunion manuellement
        log.info("🔗 Generating instant Google Meet link");
        log.info("📝 Meeting title: {}", title);
        return "https://meet.google.com/new";
    }
}
