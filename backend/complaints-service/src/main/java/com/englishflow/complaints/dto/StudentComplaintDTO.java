package com.englishflow.complaints.dto;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintPriority;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentComplaintDTO {
    private Long id;
    private Long userId;
    private TargetRole targetRole;
    private ComplaintCategory category;
    private String subject;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private Integer riskScore;
    private Boolean requiresIntervention;
    private Boolean studentConfirmed;
    private String courseType;
    private String difficulty;
    private String issueType;
    private Integer sessionCount;
    private String response;
    private Long responderId;
    private String responderRole;
    private String responderName; // NEW: responder's full name
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static StudentComplaintDTO fromComplaint(Complaint complaint, String responderName) {
        StudentComplaintDTO dto = new StudentComplaintDTO();
        dto.setId(complaint.getId());
        dto.setUserId(complaint.getUserId());
        dto.setTargetRole(complaint.getTargetRole());
        dto.setCategory(complaint.getCategory());
        dto.setSubject(complaint.getSubject());
        dto.setDescription(complaint.getDescription());
        dto.setStatus(complaint.getStatus());
        dto.setPriority(complaint.getPriority());
        dto.setRiskScore(complaint.getRiskScore());
        dto.setRequiresIntervention(complaint.getRequiresIntervention());
        dto.setStudentConfirmed(complaint.getStudentConfirmed());
        dto.setCourseType(complaint.getCourseType());
        dto.setDifficulty(complaint.getDifficulty());
        dto.setIssueType(complaint.getIssueType());
        dto.setSessionCount(complaint.getSessionCount());
        dto.setResponse(complaint.getResponse());
        dto.setResponderId(complaint.getResponderId());
        dto.setResponderRole(complaint.getResponderRole());
        dto.setResponderName(responderName);
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        return dto;
    }
}
