package com.jungle.learning.repository;

import com.jungle.learning.model.EbookMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EbookMetadataRepository extends JpaRepository<EbookMetadata, Long> {
    
    Optional<EbookMetadata> findByEbookId(Long ebookId);
    
    Optional<EbookMetadata> findByIsbn(String isbn);
}
