package com.englishflow.event.dto.live;

import lombok.Data;

import java.util.List;

@Data
public class PollDTO {
    private Long id;
    private Integer eventId;
    private String question;
    private boolean multipleChoice;
    private boolean active;
    private List<PollOptionDTO> options;

    @Data
    public static class PollOptionDTO {
        private Long id;
        private String text;
        private int voteCount;
        private boolean votedByCurrentUser;
    }
}
