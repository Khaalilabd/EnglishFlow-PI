package com.englishflow.event.dto.live;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionDTO {
    private Long id;
    private Integer eventId;
    private Long authorId;
    private String authorName;
    private String text;
    private int upvoteCount;
    private boolean upvotedByCurrentUser;
    private boolean answered;
    private boolean anonymous;
    private LocalDateTime createdAt;
}
