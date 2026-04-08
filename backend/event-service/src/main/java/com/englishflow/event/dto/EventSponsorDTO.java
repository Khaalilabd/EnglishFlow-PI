package com.englishflow.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSponsorDTO {
    private Long id;
    private String name;
    private String logo;
    private String level; // GOLD, SILVER, BRONZE, PARTNER
    private Double contributionAmount;
}
