package com.jungle.learning.controller;

import com.jungle.learning.dto.CollectionDTO;
import com.jungle.learning.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<CollectionDTO> createCollection(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        String name = (String) request.get("name");
        String description = (String) request.get("description");
        Boolean isPublic = (Boolean) request.get("isPublic");
        
        CollectionDTO collection = collectionService.createCollection(name, description, isPublic, userId);
        return ResponseEntity.ok(collection);
    }

    @PutMapping("/{collectionId}")
    public ResponseEntity<CollectionDTO> updateCollection(
            @PathVariable Long collectionId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        String name = (String) request.get("name");
        String description = (String) request.get("description");
        Boolean isPublic = (Boolean) request.get("isPublic");
        
        CollectionDTO collection = collectionService.updateCollection(
            collectionId, name, description, isPublic, userId);
        return ResponseEntity.ok(collection);
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable Long collectionId,
            @RequestHeader("X-User-Id") Long userId) {
        collectionService.deleteCollection(collectionId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{collectionId}/ebooks/{ebookId}")
    public ResponseEntity<CollectionDTO> addEbook(
            @PathVariable Long collectionId,
            @PathVariable Long ebookId,
            @RequestHeader("X-User-Id") Long userId) {
        CollectionDTO collection = collectionService.addEbookToCollection(collectionId, ebookId, userId);
        return ResponseEntity.ok(collection);
    }

    @DeleteMapping("/{collectionId}/ebooks/{ebookId}")
    public ResponseEntity<CollectionDTO> removeEbook(
            @PathVariable Long collectionId,
            @PathVariable Long ebookId,
            @RequestHeader("X-User-Id") Long userId) {
        CollectionDTO collection = collectionService.removeEbookFromCollection(collectionId, ebookId, userId);
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CollectionDTO>> getUserCollections(
            @RequestHeader("X-User-Id") Long userId) {
        List<CollectionDTO> collections = collectionService.getUserCollections(userId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/public")
    public ResponseEntity<List<CollectionDTO>> getPublicCollections() {
        List<CollectionDTO> collections = collectionService.getPublicCollections();
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionDTO> getCollection(@PathVariable Long collectionId) {
        CollectionDTO collection = collectionService.getCollection(collectionId);
        return ResponseEntity.ok(collection);
    }
}
