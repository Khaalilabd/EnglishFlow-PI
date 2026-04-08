package com.englishflow.auth.service;

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
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "EnglishFlow Recruitment";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${meeting.google.credentials-file:#{null}}")
    private String credentialsFilePath;

    @Value("${meeting.google.enabled:false}")
    private boolean enabled;

    /**
     * Crée un événement Google Calendar avec un lien Google Meet
     * Note: Nécessite Google Workspace pour fonctionner correctement
     */
    public String createMeetingLink(String title, String description, LocalDateTime startTime, int durationMinutes) {
        if (!enabled) {
            log.warn("Google Meet integration not enabled, generating instant meet link");
            return generateInstantMeetLink();
        }

        try {
            Calendar service = getCalendarService();
            
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

            // Ajouter la configuration Google Meet
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
                log.info("Google Meet link created successfully: {}", meetLink);
                return meetLink;
            } else {
                log.warn("No conference data in created event, using instant meet link");
                return generateInstantMeetLink();
            }

        } catch (Exception e) {
            log.error("Failed to create Google Meet link via Calendar API: {}. Using instant meet link instead.", e.getMessage());
            return generateInstantMeetLink();
        }
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

    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
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
        String meetingId = UUID.randomUUID().toString().substring(0, 10).replace("-", "");
        return "https://meet.google.com/" + meetingId;
    }
}