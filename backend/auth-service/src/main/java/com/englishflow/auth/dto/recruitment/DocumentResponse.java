package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.entity.ApplicationDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;
    private Long applicationId;
    private String type;
    private String documentType; // Alias for type to match frontend
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public static DocumentResponse fromEntity(ApplicationDocument document) {
        String docType = document.getType().name().toLowerCase();
        return DocumentResponse.builder()
                .id(document.getId())
                .applicationId(document.getApplication().getId())
                .type(docType)
                .documentType(docType)
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
