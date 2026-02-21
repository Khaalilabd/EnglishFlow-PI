package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionSummaryDTO {
    private String emoji;
    private Long count;
    private List<String> userNames;
    private boolean reactedByCurrentUser;
}
