package com.jungle.learning.service;

import com.jungle.learning.dto.CollectionDTO;
import com.jungle.learning.model.Collection;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.repository.CollectionRepository;
import com.jungle.learning.repository.EbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final EbookRepository ebookRepository;

    @Transactional
    public CollectionDTO createCollection(String name, String description, Boolean isPublic, Long ownerId) {
        Collection collection = new Collection();
        collection.setName(name);
        collection.setDescription(description);
        collection.setIsPublic(isPublic != null ? isPublic : false);
        collection.setOwnerId(ownerId);

        Collection saved = collectionRepository.save(collection);
        return mapToDTO(saved);
    }

    @Transactional
    public CollectionDTO updateCollection(Long collectionId, String name, String description, 
                                         Boolean isPublic, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if (!collection.getOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (name != null) collection.setName(name);
        if (description != null) collection.setDescription(description);
        if (isPublic != null) collection.setIsPublic(isPublic);

        Collection updated = collectionRepository.save(collection);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteCollection(Long collectionId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if (!collection.getOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        collectionRepository.delete(collection);
    }

    @Transactional
    public CollectionDTO addEbookToCollection(Long collectionId, Long ebookId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if (!collection.getOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        if (!collection.getEbooks().contains(ebook)) {
            collection.getEbooks().add(ebook);
            Collection updated = collectionRepository.save(collection);
            return mapToDTO(updated);
        }

        return mapToDTO(collection);
    }

    @Transactional
    public CollectionDTO removeEbookFromCollection(Long collectionId, Long ebookId, Long userId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        if (!collection.getOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        collection.getEbooks().removeIf(ebook -> ebook.getId().equals(ebookId));
        Collection updated = collectionRepository.save(collection);
        return mapToDTO(updated);
    }

    public List<CollectionDTO> getUserCollections(Long userId) {
        return collectionRepository.findByOwnerId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CollectionDTO> getPublicCollections() {
        return collectionRepository.findByIsPublicTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CollectionDTO getCollection(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
    }

    private CollectionDTO mapToDTO(Collection collection) {
        CollectionDTO dto = new CollectionDTO();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setDescription(collection.getDescription());
        dto.setIsPublic(collection.getIsPublic());
        dto.setOwnerId(collection.getOwnerId());
        dto.setEbookIds(collection.getEbooks().stream()
                .map(Ebook::getId)
                .collect(Collectors.toList()));
        dto.setEbooksCount(collection.getEbooks().size());
        dto.setCreatedAt(collection.getCreatedAt());
        dto.setUpdatedAt(collection.getUpdatedAt());
        return dto;
    }
}
