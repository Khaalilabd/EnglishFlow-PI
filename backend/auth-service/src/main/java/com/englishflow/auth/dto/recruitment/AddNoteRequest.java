package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(min = 10, max = 2000, message = "Note must be between 10 and 2000 characters")
    private String content;
}
