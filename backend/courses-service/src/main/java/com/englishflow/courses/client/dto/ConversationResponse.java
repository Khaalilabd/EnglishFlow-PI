package com.englishflow.courses.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private String type;
    private String title;
    private String description;
    private Long createdBy;
}
