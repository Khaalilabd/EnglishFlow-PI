package com.englishflow.event.dto.live;

import lombok.Data;

@Data
public class WhiteboardEventDTO {
    private Integer eventId;
    private Long userId;
    private String type; // "DRAW", "ERASE", "CLEAR", "TEXT"
    private double x;
    private double y;
    private double x2;
    private double y2;
    private String color;
    private double strokeWidth;
    private String text; // for TEXT type
}
