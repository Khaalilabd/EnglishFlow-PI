package com.englishflow.event.dto.live;

import lombok.Data;

@Data
public class ReactionDTO {
    private Integer eventId;
    private Long userId;
    private String userName;
    private String emoji; // "👏", "❤️", "😂", "🔥", "👍"
}
