package com.englishflow.messaging.dto;

import com.englishflow.messaging.model.Conversation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @NotEmpty(message = "Participant IDs are required")
    @Size(min = 1, max = 50, message = "Conversation must have between 1 and 50 participants")
    private List<Long> participantIds;
    
    @NotNull(message = "Conversation type is required")
    private Conversation.ConversationType type;
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
}
