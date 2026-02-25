package com.englishflow.event.dto;

import com.englishflow.event.enums.EventStatus;
import com.englishflow.event.enums.EventType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    
    private Integer id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @NotNull(message = "Event type is required")
    private EventType type;
    
    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Max participants is required")
    @Min(value = 1, message = "Max participants must be at least 1")
    private Integer maxParticipants;
    
    private Integer currentParticipants;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Long creatorId;
    
    private String image;
    
    private EventStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
