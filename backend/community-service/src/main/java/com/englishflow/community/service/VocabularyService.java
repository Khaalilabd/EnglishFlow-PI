package com.englishflow.community.service;

import com.englishflow.community.dto.SaveVocabularyRequest;
import com.englishflow.community.dto.VocabularyStatsDTO;
import com.englishflow.community.dto.VocabularyWordDTO;
import com.englishflow.community.entity.VocabularyWord;
import com.englishflow.community.repository.VocabularyWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VocabularyService {
    
    private final VocabularyWordRepository vocabularyWordRepository;
    
    @Transactional
    public VocabularyWordDTO saveWord(Long userId, SaveVocabularyRequest request) {
        log.info("Saving word '{}' for user {}", request.getWord(), userId);
        
        // Check if word already exists
        if (vocabularyWordRepository.existsByUserIdAndWord(userId, request.getWord())) {
            throw new RuntimeException("Word already exists in your vocabulary");
        }
        
        VocabularyWord word = new VocabularyWord();
        word.setUserId(userId);
        word.setWord(request.getWord());
        word.setDefinition(request.getDefinition());
        word.setPhonetic(request.getPhonetic());
        word.setPartOfSpeech(request.getPartOfSpeech());
        word.setExample(request.getExample());
        word.setSynonyms(request.getSynonyms());
        word.setAntonyms(request.getAntonyms());
        word.setAudioUrl(request.getAudioUrl());
        word.setSourceTopicId(request.getSourceTopicId());
        word.setMasteryLevel(VocabularyWord.MasteryLevel.NEW);
        word.setReviewCount(0);
        
        VocabularyWord saved = vocabularyWordRepository.save(word);
        log.info("Word '{}' saved successfully with ID {}", saved.getWord(), saved.getId());
        
        return convertToDTO(saved);
    }
    
    public Page<VocabularyWordDTO> getUserVocabulary(Long userId, int page, int size, String sortBy) {
        log.info("Fetching vocabulary for user {} (page: {}, size: {}, sort: {})", userId, page, size, sortBy);
        
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<VocabularyWord> words = vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return words.map(this::convertToDTO);
    }
    
    public Page<VocabularyWordDTO> getUserVocabularyByLevel(Long userId, String level, int page, int size, String sortBy) {
        log.info("Fetching vocabulary for user {} with level {} (page: {}, size: {}, sort: {})", userId, level, page, size, sortBy);
        
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        VocabularyWord.MasteryLevel masteryLevel = VocabularyWord.MasteryLevel.valueOf(level);
        Page<VocabularyWord> words = vocabularyWordRepository.findByUserIdAndMasteryLevelOrderByCreatedAtDesc(userId, masteryLevel, pageable);
        return words.map(this::convertToDTO);
    }
    
    public Page<VocabularyWordDTO> searchVocabulary(Long userId, String search, int page, int size) {
        log.info("Searching vocabulary for user {} with query: {}", userId, search);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VocabularyWord> words = vocabularyWordRepository.searchByUserIdAndWord(userId, search, pageable);
        
        return words.map(this::convertToDTO);
    }
    
    public VocabularyStatsDTO getUserStats(Long userId) {
        log.info("Fetching vocabulary stats for user {}", userId);
        
        Long totalWords = vocabularyWordRepository.countByUserId(userId);
        Long newWords = vocabularyWordRepository.countByUserIdAndMasteryLevel(userId, VocabularyWord.MasteryLevel.NEW);
        Long learningWords = vocabularyWordRepository.countByUserIdAndMasteryLevel(userId, VocabularyWord.MasteryLevel.LEARNING);
        
        // Calculate total reviews
        List<VocabularyWord> allWords = vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(
            userId, 
            PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent();
        
        Long totalReviews = allWords.stream()
            .mapToLong(w -> w.getReviewCount() != null ? w.getReviewCount() : 0)
            .sum();
        
        return new VocabularyStatsDTO(totalWords, newWords, learningWords, 0L, 0L, totalReviews);
    }
    
    @Transactional
    public VocabularyWordDTO markAsReviewed(Long userId, Long wordId) {
        log.info("Marking word {} as reviewed for user {}", wordId, userId);
        
        VocabularyWord word = vocabularyWordRepository.findById(wordId)
            .orElseThrow(() -> new RuntimeException("Word not found"));
        
        if (!word.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to vocabulary word");
        }
        
        word.setReviewCount(word.getReviewCount() + 1);
        word.setLastReviewedAt(LocalDateTime.now());
        
        // Simple progression: NEW -> LEARNING after first review
        if (word.getReviewCount() >= 1 && word.getMasteryLevel() == VocabularyWord.MasteryLevel.NEW) {
            word.setMasteryLevel(VocabularyWord.MasteryLevel.LEARNING);
        }
        
        VocabularyWord updated = vocabularyWordRepository.save(word);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteWord(Long userId, Long wordId) {
        log.info("Deleting word {} for user {}", wordId, userId);
        
        VocabularyWord word = vocabularyWordRepository.findById(wordId)
            .orElseThrow(() -> new RuntimeException("Word not found"));
        
        if (!word.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to vocabulary word");
        }
        
        vocabularyWordRepository.delete(word);
        log.info("Word '{}' deleted successfully", word.getWord());
    }
    
    public boolean isWordSaved(Long userId, String word) {
        return vocabularyWordRepository.existsByUserIdAndWord(userId, word);
    }
    
    public List<VocabularyWordDTO> getAllUserVocabulary(Long userId) {
        log.info("Fetching all vocabulary for user {} (for export)", userId);
        
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "word"));
        Page<VocabularyWord> words = vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return words.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private VocabularyWordDTO convertToDTO(VocabularyWord word) {
        return new VocabularyWordDTO(
            word.getId(),
            word.getWord(),
            word.getDefinition(),
            word.getPhonetic(),
            word.getPartOfSpeech(),
            word.getExample(),
            word.getSynonyms(),
            word.getAntonyms(),
            word.getAudioUrl(),
            word.getSourceTopicId(),
            word.getMasteryLevel(),
            word.getReviewCount(),
            word.getLastReviewedAt(),
            word.getCreatedAt()
        );
    }
}
