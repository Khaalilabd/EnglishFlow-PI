package com.englishflow.courses.dto;

import com.englishflow.courses.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonMediaDTO {
    private Long id;
    private String url;
    private LessonType mediaType;
    private Integer position;
    private String title;
    private String description;
    private Long lessonId;
}
