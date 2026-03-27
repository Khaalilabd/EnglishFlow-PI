package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbookMetadataDTO {
    private Long id;
    private Long ebookId;
    private String author;
    private String publisher;
    private String isbn;
    private Integer totalPages;
    private Integer estimatedReadTimeMinutes;
    private String language;
    private String edition;
    private LocalDate publicationDate;
    private List<String> keywords;
    private String tableOfContents;
}
