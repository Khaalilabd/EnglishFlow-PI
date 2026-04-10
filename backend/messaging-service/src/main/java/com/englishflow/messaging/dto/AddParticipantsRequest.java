package com.englishflow.messaging.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddParticipantsRequest {
    
    @NotNull(message = "Participant IDs are required")
    @NotEmpty(message = "At least one participant ID is required")
    private List<Long> participantIds;
}
