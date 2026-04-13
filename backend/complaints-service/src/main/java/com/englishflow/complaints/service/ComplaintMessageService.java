package com.englishflow.complaints.service;

import com.englishflow.complaints.dto.ComplaintMessageDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintMessage;
import com.englishflow.complaints.repository.ComplaintMessageRepository;
import com.englishflow.complaints.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintMessageService {
    
    private final ComplaintMessageRepository messageRepository;
    private final ComplaintRepository complaintRepository;
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    public ComplaintMessageDTO createMessage(ComplaintMessage message) {
        log.info("Creating message for complaint: {}", message.getComplaintId());
        ComplaintMessage saved = messageRepository.save(message);
        
        // Update the complaint's response field with the latest message if it's from admin/tutor
        if (!message.getAuthorRole().equals("STUDENT")) {
            try {
                Complaint complaint = complaintRepository.findById(message.getComplaintId())
                        .orElseThrow(() -> new RuntimeException("Complaint not found"));
                complaint.setResponse(message.getContent());
                complaint.setResponderId(message.getAuthorId());
                complaint.setResponderRole(message.getAuthorRole());
                complaintRepository.save(complaint);
                log.info("Updated complaint response field for complaint: {}", message.getComplaintId());
            } catch (Exception e) {
                log.error("Failed to update complaint response field", e);
            }
        }
        
        String authorName = getAuthorName(saved.getAuthorId());
        return ComplaintMessageDTO.fromEntity(saved, authorName);
    }
    
    public List<ComplaintMessageDTO> getMessagesByComplaintId(Long complaintId) {
        log.info("Fetching messages for complaint: {}", complaintId);
        List<ComplaintMessage> messages = messageRepository.findByComplaintIdOrderByTimestampAsc(complaintId);
        List<ComplaintMessageDTO> dtos = new ArrayList<>();
        
        for (ComplaintMessage message : messages) {
            String authorName = getAuthorName(message.getAuthorId());
            dtos.add(ComplaintMessageDTO.fromEntity(message, authorName));
        }
        
        return dtos;
    }
    
    private String getAuthorName(Long authorId) {
        try {
            String url = authServiceUrl + "/users/" + authorId + "/public";
            log.info("🔍 Fetching author name from URL: {}", url);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("📥 Response received: {}", response);
            if (response != null) {
                String firstName = (String) response.getOrDefault("firstName", "");
                String lastName = (String) response.getOrDefault("lastName", "");
                String fullName = (firstName + " " + lastName).trim();
                log.info("✅ Author name resolved: {} (from authorId: {})", fullName, authorId);
                return fullName.isEmpty() ? "User#" + authorId : fullName;
            }
        } catch (Exception e) {
            log.error("❌ Failed to fetch author name for authorId: {} from URL: {}", authorId, authServiceUrl + "/users/" + authorId + "/public", e);
        }
        
        log.warn("⚠️ Returning fallback name for authorId: {}", authorId);
        return "User#" + authorId;
    }
}
