package com.englishflow.courses.dto;

import com.englishflow.courses.enums.DayOfWeek;
import com.englishflow.courses.enums.TutorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailabilityDTO {
    private Long id;
    private Long tutorId;
    private String tutorName;
    private Set<DayOfWeek> availableDays = new HashSet<>();
    private List<TimeSlotDTO> timeSlots = new ArrayList<>();
    private Integer maxStudentsCapacity;
    private Integer currentStudentsCount;
    private Integer availableCapacity;
    private Double capacityPercentage;
    private Set<String> categories = new HashSet<>(); // Dynamic category names
    private Set<String> levels = new HashSet<>(); // A1, A2, B1, B2, C1, C2
    private TutorStatus status;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
}
