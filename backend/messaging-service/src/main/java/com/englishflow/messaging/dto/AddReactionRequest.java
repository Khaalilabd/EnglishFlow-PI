package com.englishflow.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddReactionRequest {
    
    @NotBlank(message = "Emoji is required")
    @Size(max = 10, message = "Emoji must not exceed 10 characters")
    private String emoji;
}
