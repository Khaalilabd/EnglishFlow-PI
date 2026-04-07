package com.englishflow.event.dto.live;

import lombok.Data;

@Data
public class WebRTCSignalDTO {
    private Integer eventId;
    private Long fromUserId;
    private Long toUserId;       // null = broadcast to all
    private String type;         // "offer" | "answer" | "ice-candidate" | "join" | "leave"
    private String sdp;          // for offer/answer
    private String candidate;    // for ice-candidate
    private String sdpMid;
    private Integer sdpMLineIndex;
    private String userName;
}
