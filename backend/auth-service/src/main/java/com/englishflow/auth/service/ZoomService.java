package com.englishflow.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ZoomService {

    private static final String ZOOM_API_BASE_URL = "https://api.zoom.us/v2";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${meeting.zoom.account-id:#{null}}")
    private String accountId;

    @Value("${meeting.zoom.client-id:#{null}}")
    private String clientId;

    @Value("${meeting.zoom.client-secret:#{null}}")
    private String clientSecret;

    @Value("${meeting.zoom.enabled:false}")
    private boolean enabled;

    /**
     * Crée une réunion Zoom et retourne le lien
     */
    public Map<String, String> createMeeting(String title, LocalDateTime startTime, int durationMinutes) {
        Map<String, String> result = new HashMap<>();
        
        if (!enabled || clientId == null || clientSecret == null) {
            log.warn("Zoom integration not enabled or not configured, generating placeholder");
            return generatePlaceholderMeeting();
        }

        try {
            String accessToken = getAccessToken();
            
            // Préparer les données de la réunion
            Map<String, Object> meetingData = new HashMap<>();
            meetingData.put("topic", title);
            meetingData.put("type", 2); // Scheduled meeting
            meetingData.put("start_time", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            meetingData.put("duration", durationMinutes);
            meetingData.put("timezone", "Africa/Tunis");
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("host_video", true);
            settings.put("participant_video", true);
            settings.put("join_before_host", false);
            settings.put("mute_upon_entry", true);
            settings.put("waiting_room", true);
            settings.put("audio", "both");
            meetingData.put("settings", settings);

            // Créer la réunion
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(meetingData),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(ZOOM_API_BASE_URL + "/users/me/meetings")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonNode jsonResponse = objectMapper.readTree(response.body().string());
                    
                    result.put("meetingLink", jsonResponse.get("join_url").asText());
                    result.put("meetingId", jsonResponse.get("id").asText());
                    result.put("password", jsonResponse.has("password") ? jsonResponse.get("password").asText() : "");
                    
                    log.info("Zoom meeting created successfully: {}", result.get("meetingId"));
                    return result;
                } else {
                    log.error("Failed to create Zoom meeting: {}", response.code());
                    return generatePlaceholderMeeting();
                }
            }

        } catch (Exception e) {
            log.error("Error creating Zoom meeting", e);
            return generatePlaceholderMeeting();
        }
    }

    private String getAccessToken() throws IOException {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "account_credentials")
                .add("account_id", accountId)
                .build();

        Request request = new Request.Builder()
                .url("https://zoom.us/oauth/token")
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.body().string());
                return jsonResponse.get("access_token").asText();
            } else {
                throw new IOException("Failed to get Zoom access token: " + response.code());
            }
        }
    }

    private Map<String, String> generatePlaceholderMeeting() {
        Map<String, String> result = new HashMap<>();
        String meetingId = String.valueOf(System.currentTimeMillis() % 10000000000L);
        String password = UUID.randomUUID().toString().substring(0, 6);
        result.put("meetingLink", "https://zoom.us/j/" + meetingId + "?pwd=" + password);
        result.put("meetingId", meetingId);
        result.put("password", password);
        return result;
    }
}
