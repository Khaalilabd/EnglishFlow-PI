package com.englishflow.courses.client;

import com.englishflow.courses.client.dto.AddParticipantsRequest;
import com.englishflow.courses.client.dto.ConversationResponse;
import com.englishflow.courses.client.dto.CreateGroupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceClient {
    
    private final RestTemplate restTemplate;
    private static final String MESSAGING_SERVICE_URL = "http://messaging-service/messaging/internal";
    
    /**
     * Créer un groupe de discussion pour un pack
     */
    public Long createPackGroup(String packName, String description, Long tutorId, List<Long> studentIds) {
        try {
            CreateGroupRequest request = new CreateGroupRequest();
            request.setParticipantIds(studentIds);
            request.setType("GROUP");
            request.setTitle("Pack: " + packName);
            request.setDescription(description != null ? description : "Groupe de discussion pour le pack " + packName);
            
            log.info("Creating group for pack '{}' with tutor {} and {} students", 
                    packName, tutorId, studentIds.size());
            
            ResponseEntity<ConversationResponse> response = restTemplate.postForEntity(
                MESSAGING_SERVICE_URL + "/conversations",
                request,
                ConversationResponse.class
            );
            
            if (response.getBody() != null) {
                Long conversationId = response.getBody().getId();
                log.info("Group created successfully with ID: {}", conversationId);
                return conversationId;
            }
            
            log.error("Failed to create group: response body is null");
            return null;
            
        } catch (Exception e) {
            log.error("Error creating group for pack '{}': {}", packName, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Ajouter un étudiant au groupe du pack
     */
    public boolean addStudentToPackGroup(Long conversationId, Long studentId) {
        try {
            List<Long> participantIds = List.of(studentId);
            
            log.info("Adding student {} to conversation {}", studentId, conversationId);
            
            restTemplate.postForEntity(
                MESSAGING_SERVICE_URL + "/conversations/" + conversationId + "/participants",
                participantIds,
                ConversationResponse.class
            );
            
            log.info("Student {} added successfully to conversation {}", studentId, conversationId);
            return true;
            
        } catch (Exception e) {
            log.error("Error adding student {} to conversation {}: {}", 
                    studentId, conversationId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Retirer un étudiant du groupe du pack
     */
    public boolean removeStudentFromPackGroup(Long conversationId, Long studentId) {
        try {
            log.info("Removing student {} from conversation {}", studentId, conversationId);
            
            restTemplate.delete(
                MESSAGING_SERVICE_URL + "/conversations/" + conversationId + "/participants/" + studentId
            );
            
            log.info("Student {} removed successfully from conversation {}", studentId, conversationId);
            return true;
            
        } catch (Exception e) {
            log.error("Error removing student {} from conversation {}: {}", 
                    studentId, conversationId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Supprimer le groupe du pack
     */
    public boolean deletePackGroup(Long conversationId) {
        try {
            log.info("Deleting conversation {}", conversationId);
            
            restTemplate.delete(MESSAGING_SERVICE_URL + "/conversations/" + conversationId);
            
            log.info("Conversation {} deleted successfully", conversationId);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting conversation {}: {}", conversationId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Envoyer un message système dans le groupe
     */
    public boolean sendSystemMessage(Long conversationId, String message) {
        try {
            log.info("Sending system message to conversation {}", conversationId);
            
            // Créer une requête simple avec le message
            var request = new java.util.HashMap<String, Object>();
            request.put("content", message);
            request.put("messageType", "TEXT");
            
            restTemplate.postForEntity(
                MESSAGING_SERVICE_URL + "/conversations/" + conversationId + "/system-message",
                request,
                Object.class
            );
            
            log.info("System message sent successfully to conversation {}", conversationId);
            return true;
            
        } catch (Exception e) {
            log.error("Error sending system message to conversation {}: {}", 
                    conversationId, e.getMessage(), e);
            return false;
        }
    }
}
