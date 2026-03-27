package com.englishflow.club.dto;

import com.englishflow.club.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    
    private Integer id;
    
    @NotBlank(message = "Task text is required")
    private String text;
    
    @NotNull(message = "Task status is required")
    private TaskStatus status;
    
    @NotNull(message = "Club ID is required")
    private Integer clubId;
    
    private Integer createdBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
