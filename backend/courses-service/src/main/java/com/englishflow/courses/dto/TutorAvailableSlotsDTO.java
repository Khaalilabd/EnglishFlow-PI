package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailableSlotsDTO {
    private Long tutorId;
    private String tutorName;
    private boolean hasAvailability;
    private List<AvailableTimeSlotDTO> availableSlots = new ArrayList<>();
}
