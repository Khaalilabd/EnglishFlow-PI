package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.entity.ApplicationNote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {

    private Long id;
    private String content;
    private Long createdBy;
    private LocalDateTime createdAt;

    public static NoteResponse fromEntity(ApplicationNote note) {
        return NoteResponse.builder()
                .id(note.getId())
                .content(note.getContent())
                .createdBy(note.getCreatedBy())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
