package com.englishflow.courses.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    private List<Long> participantIds;
    private String type = "GROUP";
    private String title;
    private String description;
}
