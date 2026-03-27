package com.jungle.learning.controller;

import com.jungle.learning.dto.EbookChapterDTO;
import com.jungle.learning.dto.EbookMetadataDTO;
import com.jungle.learning.service.EbookMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ebooks")
@RequiredArgsConstructor
public class EbookMetadataController {

    private final EbookMetadataService metadataService;

    // Metadata endpoints
    @PostMapping("/{ebookId}/metadata")
    public ResponseEntity<EbookMetadataDTO> createOrUpdateMetadata(
            @PathVariable Long ebookId,
            @RequestBody EbookMetadataDTO dto) {
        EbookMetadataDTO metadata = metadataService.createOrUpdateMetadata(ebookId, dto);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/{ebookId}/metadata")
    public ResponseEntity<EbookMetadataDTO> getMetadata(@PathVariable Long ebookId) {
        EbookMetadataDTO metadata = metadataService.getMetadata(ebookId);
        return metadata != null ? ResponseEntity.ok(metadata) : ResponseEntity.notFound().build();
    }

    // Chapter endpoints
    @PostMapping("/{ebookId}/chapters")
    public ResponseEntity<EbookChapterDTO> createChapter(
            @PathVariable Long ebookId,
            @RequestBody EbookChapterDTO dto) {
        EbookChapterDTO chapter = metadataService.createChapter(ebookId, dto);
        return ResponseEntity.ok(chapter);
    }

    @PutMapping("/chapters/{chapterId}")
    public ResponseEntity<EbookChapterDTO> updateChapter(
            @PathVariable Long chapterId,
            @RequestBody EbookChapterDTO dto) {
        EbookChapterDTO chapter = metadataService.updateChapter(chapterId, dto);
        return ResponseEntity.ok(chapter);
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long chapterId) {
        metadataService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ebookId}/chapters")
    public ResponseEntity<List<EbookChapterDTO>> getChapters(@PathVariable Long ebookId) {
        List<EbookChapterDTO> chapters = metadataService.getChapters(ebookId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{ebookId}/chapters/free")
    public ResponseEntity<List<EbookChapterDTO>> getFreeChapters(@PathVariable Long ebookId) {
        List<EbookChapterDTO> chapters = metadataService.getFreeChapters(ebookId);
        return ResponseEntity.ok(chapters);
    }
}
