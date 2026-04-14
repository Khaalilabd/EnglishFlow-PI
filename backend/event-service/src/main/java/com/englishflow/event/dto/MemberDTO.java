package com.englishflow.event.dto;

import com.englishflow.event.enums.RankType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDTO {
    private Integer id;
    private Integer clubId;
    private Long userId;
    private RankType rank;
    // joinedAt intentionally omitted — club-service returns microseconds format
    // that conflicts with our LocalDateTime deserializer
}
