package com.englishflow.complaints.dto;

import com.englishflow.complaints.entity.ComplaintWorkflow;
import com.englishflow.complaints.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintWorkflowDTO {
    private Long id;
    private Long complaintId;
    private ComplaintStatus fromStatus;
    private ComplaintStatus toStatus;
    private Long actorId;
    private String actorRole;
    private String actorName; // Full name of the actor
    private String comment;
    private Boolean isEscalation;
    private String escalationReason;
    private LocalDateTime timestamp;
    
    public static ComplaintWorkflowDTO fromEntity(ComplaintWorkflow workflow, String actorName) {
        ComplaintWorkflowDTO dto = new ComplaintWorkflowDTO();
        dto.setId(workflow.getId());
        dto.setComplaintId(workflow.getComplaintId());
        dto.setFromStatus(workflow.getFromStatus());
        dto.setToStatus(workflow.getToStatus());
        dto.setActorId(workflow.getActorId());
        dto.setActorRole(workflow.getActorRole());
        dto.setActorName(actorName);
        dto.setComment(workflow.getComment());
        dto.setIsEscalation(workflow.getIsEscalation());
        dto.setEscalationReason(workflow.getEscalationReason());
        dto.setTimestamp(workflow.getTimestamp());
        return dto;
    }
}
