package com.englishflow.event.dto;

import com.englishflow.event.enums.RankType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer id;
    private Integer clubId;
    private Long userId;
    private RankType rank;
    private LocalDateTime joinedAt;
}
