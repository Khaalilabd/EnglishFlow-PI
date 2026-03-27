package com.englishflow.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateGroupRequest {
    
    @NotBlank(message = "Group title is required")
    @Size(min = 1, max = 255, message = "Group title must be between 1 and 255 characters")
    private String title;
    
    @Size(max = 500, message = "Group description must not exceed 500 characters")
    private String description;
}
