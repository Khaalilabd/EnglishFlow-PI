package com.englishflow.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinEventRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
}
