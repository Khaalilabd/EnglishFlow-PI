package com.englishflow.complaints.service;

import com.englishflow.complaints.dto.ComplaintWorkflowDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintWorkflow;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.repository.ComplaintWorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintWorkflowService {
    
    private final ComplaintWorkflowRepository workflowRepository;
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Transactional
    public void recordStatusChange(Complaint complaint, ComplaintStatus oldStatus, 
                                   Long actorId, String actorRole, String comment) {
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setComplaintId(complaint.getId());
        workflow.setFromStatus(oldStatus);
        workflow.setToStatus(complaint.getStatus());
        workflow.setActorId(actorId);
        workflow.setActorRole(actorRole);
        workflow.setComment(comment);
        
        // Check if this is an escalation
        if (isEscalation(oldStatus, complaint.getStatus())) {
            workflow.setIsEscalation(true);
            workflow.setEscalationReason("Status escalated from " + oldStatus + " to " + complaint.getStatus());
        }
        
        workflowRepository.save(workflow);
        log.info("Workflow recorded for complaint {} - {} -> {}", 
                 complaint.getId(), oldStatus, complaint.getStatus());
    }
    
    private boolean isEscalation(ComplaintStatus from, ComplaintStatus to) {
        // Escalation if moving from resolved/rejected back to open/in_progress
        return (from == ComplaintStatus.RESOLVED || from == ComplaintStatus.REJECTED) &&
               (to == ComplaintStatus.OPEN || to == ComplaintStatus.IN_PROGRESS);
    }
    
    public List<ComplaintWorkflow> getComplaintHistory(Long complaintId) {
        return workflowRepository.findByComplaintIdOrderByTimestampDesc(complaintId);
    }
    
    public List<ComplaintWorkflowDTO> getComplaintHistoryWithActorNames(Long complaintId) {
        List<ComplaintWorkflow> workflows = workflowRepository.findByComplaintIdOrderByTimestampDesc(complaintId);
        List<ComplaintWorkflowDTO> dtos = new ArrayList<>();
        
        for (ComplaintWorkflow workflow : workflows) {
            String actorName = getActorName(workflow.getActorId(), workflow.getActorRole());
            dtos.add(ComplaintWorkflowDTO.fromEntity(workflow, actorName));
        }
        
        return dtos;
    }
    
    private String getActorName(Long actorId, String actorRole) {
        if (actorId == null || actorId == 0L) {
            return "System";
        }
        
        try {
            String url = authServiceUrl + "/users/" + actorId + "/public";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                String firstName = (String) response.getOrDefault("firstName", "");
                String lastName = (String) response.getOrDefault("lastName", "");
                String fullName = (firstName + " " + lastName).trim();
                return fullName.isEmpty() ? actorRole : fullName;
            }
        } catch (Exception e) {
            log.error("Failed to fetch actor name for actorId: {}", actorId, e);
        }
        
        return actorRole;
    }
    
    @Transactional
    public void checkAndEscalateOverdueComplaints(List<Complaint> complaints) {
        LocalDateTime now = LocalDateTime.now();
        
        for (Complaint complaint : complaints) {
            long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS
                    .between(complaint.getCreatedAt(), now);
            
            boolean shouldEscalate = switch (complaint.getPriority()) {
                case CRITICAL -> daysSinceCreation > 1 && complaint.getStatus() == ComplaintStatus.OPEN;
                case HIGH -> daysSinceCreation > 3 && complaint.getStatus() == ComplaintStatus.OPEN;
                case MEDIUM -> daysSinceCreation > 7 && complaint.getStatus() == ComplaintStatus.OPEN;
                case LOW -> daysSinceCreation > 14 && complaint.getStatus() == ComplaintStatus.OPEN;
            };
            
            if (shouldEscalate) {
                escalateComplaint(complaint);
            }
        }
    }
    
    private void escalateComplaint(Complaint complaint) {
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setComplaintId(complaint.getId());
        workflow.setFromStatus(complaint.getStatus());
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);
        workflow.setActorId(0L); // System
        workflow.setActorRole("SYSTEM");
        workflow.setIsEscalation(true);
        workflow.setEscalationReason("Automatic escalation due to overdue complaint");
        
        workflowRepository.save(workflow);
        log.warn("Complaint {} escalated due to overdue status", complaint.getId());
    }
}
