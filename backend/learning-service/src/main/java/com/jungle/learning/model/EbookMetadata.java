package com.jungle.learning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ebook_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbookMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ebook_id")
    private Ebook ebook;

    private String author;
    
    private String publisher;
    
    private String isbn;
    
    @Column(name = "total_pages")
    private Integer totalPages;
    
    @Column(name = "estimated_read_time_minutes")
    private Integer estimatedReadTimeMinutes;
    
    private String language;
    
    private String edition;
    
    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @ElementCollection
    @CollectionTable(name = "ebook_keywords", joinColumns = @JoinColumn(name = "metadata_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
    
    @Column(name = "table_of_contents", length = 1000)
    private String tableOfContents;
}
