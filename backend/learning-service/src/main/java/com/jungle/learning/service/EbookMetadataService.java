package com.jungle.learning.service;

import com.jungle.learning.dto.EbookChapterDTO;
import com.jungle.learning.dto.EbookMetadataDTO;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.EbookChapter;
import com.jungle.learning.model.EbookMetadata;
import com.jungle.learning.repository.EbookChapterRepository;
import com.jungle.learning.repository.EbookMetadataRepository;
import com.jungle.learning.repository.EbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EbookMetadataService {

    private final EbookMetadataRepository metadataRepository;
    private final EbookChapterRepository chapterRepository;
    private final EbookRepository ebookRepository;

    @Transactional
    public EbookMetadataDTO createOrUpdateMetadata(Long ebookId, EbookMetadataDTO dto) {
        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        EbookMetadata metadata = metadataRepository.findByEbookId(ebookId)
                .orElseGet(() -> {
                    EbookMetadata newMetadata = new EbookMetadata();
                    newMetadata.setEbook(ebook);
                    return newMetadata;
                });

        metadata.setAuthor(dto.getAuthor());
        metadata.setPublisher(dto.getPublisher());
        metadata.setIsbn(dto.getIsbn());
        metadata.setTotalPages(dto.getTotalPages());
        metadata.setEstimatedReadTimeMinutes(dto.getEstimatedReadTimeMinutes());
        metadata.setLanguage(dto.getLanguage());
        metadata.setEdition(dto.getEdition());
        metadata.setPublicationDate(dto.getPublicationDate());
        metadata.setKeywords(dto.getKeywords());
        metadata.setTableOfContents(dto.getTableOfContents());

        EbookMetadata saved = metadataRepository.save(metadata);
        return mapToDTO(saved);
    }

    public EbookMetadataDTO getMetadata(Long ebookId) {
        return metadataRepository.findByEbookId(ebookId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Transactional
    public EbookChapterDTO createChapter(Long ebookId, EbookChapterDTO dto) {
        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        EbookChapter chapter = new EbookChapter();
        chapter.setEbook(ebook);
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setOrderIndex(dto.getOrderIndex());
        chapter.setStartPage(dto.getStartPage());
        chapter.setEndPage(dto.getEndPage());
        chapter.setFileUrl(dto.getFileUrl());
        chapter.setIsFree(dto.getIsFree() != null ? dto.getIsFree() : false);

        EbookChapter saved = chapterRepository.save(chapter);
        return mapChapterToDTO(saved);
    }

    @Transactional
    public EbookChapterDTO updateChapter(Long chapterId, EbookChapterDTO dto) {
        EbookChapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setOrderIndex(dto.getOrderIndex());
        chapter.setStartPage(dto.getStartPage());
        chapter.setEndPage(dto.getEndPage());
        chapter.setFileUrl(dto.getFileUrl());
        chapter.setIsFree(dto.getIsFree());

        EbookChapter updated = chapterRepository.save(chapter);
        return mapChapterToDTO(updated);
    }

    @Transactional
    public void deleteChapter(Long chapterId) {
        chapterRepository.deleteById(chapterId);
    }

    public List<EbookChapterDTO> getChapters(Long ebookId) {
        return chapterRepository.findByEbookIdOrderByOrderIndexAsc(ebookId)
                .stream()
                .map(this::mapChapterToDTO)
                .collect(Collectors.toList());
    }

    public List<EbookChapterDTO> getFreeChapters(Long ebookId) {
        return chapterRepository.findByEbookIdAndIsFreeTrue(ebookId)
                .stream()
                .map(this::mapChapterToDTO)
                .collect(Collectors.toList());
    }

    private EbookMetadataDTO mapToDTO(EbookMetadata metadata) {
        EbookMetadataDTO dto = new EbookMetadataDTO();
        dto.setId(metadata.getId());
        dto.setEbookId(metadata.getEbook().getId());
        dto.setAuthor(metadata.getAuthor());
        dto.setPublisher(metadata.getPublisher());
        dto.setIsbn(metadata.getIsbn());
        dto.setTotalPages(metadata.getTotalPages());
        dto.setEstimatedReadTimeMinutes(metadata.getEstimatedReadTimeMinutes());
        dto.setLanguage(metadata.getLanguage());
        dto.setEdition(metadata.getEdition());
        dto.setPublicationDate(metadata.getPublicationDate());
        dto.setKeywords(metadata.getKeywords());
        dto.setTableOfContents(metadata.getTableOfContents());
        return dto;
    }

    private EbookChapterDTO mapChapterToDTO(EbookChapter chapter) {
        EbookChapterDTO dto = new EbookChapterDTO();
        dto.setId(chapter.getId());
        dto.setEbookId(chapter.getEbook().getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setOrderIndex(chapter.getOrderIndex());
        dto.setStartPage(chapter.getStartPage());
        dto.setEndPage(chapter.getEndPage());
        dto.setFileUrl(chapter.getFileUrl());
        dto.setIsFree(chapter.getIsFree());
        return dto;
    }
}
