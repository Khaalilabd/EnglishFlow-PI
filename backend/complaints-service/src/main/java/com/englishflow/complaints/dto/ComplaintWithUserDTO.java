package com.englishflow.complaints.dto;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintPriority;
import com.englishflow.complaints.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintWithUserDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private ComplaintCategory category;
    private String subject;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private Integer riskScore;
    private Boolean requiresIntervention;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long daysSinceCreation;
    private Boolean isOverdue;
    private List<String> escalationHistory;
    
    // Additional details based on category
    private String courseType;
    private String difficulty;
    private String issueType;
    private Integer sessionCount;
    private Long responderId;
    private String responderRole;
    
    public static ComplaintWithUserDTO fromComplaint(Complaint complaint, String username, String email) {
        ComplaintWithUserDTO dto = new ComplaintWithUserDTO();
        dto.setId(complaint.getId());
        dto.setUserId(complaint.getUserId());
        dto.setUsername(username);
        dto.setUserEmail(email);
        dto.setCategory(complaint.getCategory());
        dto.setSubject(complaint.getSubject());
        dto.setDescription(complaint.getDescription());
        dto.setStatus(complaint.getStatus());
        dto.setPriority(complaint.getPriority());
        dto.setRiskScore(complaint.getRiskScore());
        dto.setRequiresIntervention(complaint.getRequiresIntervention());
        dto.setResponse(complaint.getResponse());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        
        // Additional details
        dto.setCourseType(complaint.getCourseType());
        dto.setDifficulty(complaint.getDifficulty());
        dto.setIssueType(complaint.getIssueType());
        dto.setSessionCount(complaint.getSessionCount());
        dto.setResponderId(complaint.getResponderId());
        dto.setResponderRole(complaint.getResponderRole());
        
        // Calculate days since creation
        long days = java.time.temporal.ChronoUnit.DAYS.between(complaint.getCreatedAt(), LocalDateTime.now());
        dto.setDaysSinceCreation(days);
        
        // Check if overdue based on priority
        dto.setIsOverdue(isComplaintOverdue(complaint, days));
        
        return dto;
    }
    
    private static boolean isComplaintOverdue(Complaint complaint, long days) {
        return switch (complaint.getPriority()) {
            case CRITICAL -> days > 1;
            case HIGH -> days > 3;
            case MEDIUM -> days > 7;
            case LOW -> days > 14;
        };
    }
}
