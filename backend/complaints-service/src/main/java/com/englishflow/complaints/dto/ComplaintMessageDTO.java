package com.englishflow.complaints.dto;

import com.englishflow.complaints.entity.ComplaintMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintMessageDTO {
    private Long id;
    private Long complaintId;
    private Long authorId;
    private String author; // Full name
    private String authorRole;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isAdmin; // Helper for frontend
    
    public static ComplaintMessageDTO fromEntity(ComplaintMessage message, String authorName) {
        ComplaintMessageDTO dto = new ComplaintMessageDTO();
        dto.setId(message.getId());
        dto.setComplaintId(message.getComplaintId());
        dto.setAuthorId(message.getAuthorId());
        dto.setAuthor(authorName);
        dto.setAuthorRole(message.getAuthorRole());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setIsAdmin(!message.getAuthorRole().equals("STUDENT"));
        return dto;
    }
}
