package com.englishflow.event.dto.live;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HandRaiseDTO {
    private Long id;
    private Integer eventId;
    private Long userId;
    private String userName;
    private LocalDateTime raisedAt;

    // Sent when broadcasting the full queue
    private List<HandRaiseDTO> queue;
}
