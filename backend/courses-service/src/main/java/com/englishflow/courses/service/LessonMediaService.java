package com.englishflow.courses.service;

import com.englishflow.courses.dto.LessonMediaDTO;
import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.entity.LessonMedia;
import com.englishflow.courses.repository.LessonMediaRepository;
import com.englishflow.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonMediaService implements ILessonMediaService {
    
    private final LessonMediaRepository mediaRepository;
    private final LessonRepository lessonRepository;
    
    @Override
    @Transactional
    public LessonMediaDTO createMedia(LessonMediaDTO mediaDTO) {
        Lesson lesson = lessonRepository.findById(mediaDTO.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        LessonMedia media = new LessonMedia();
        media.setUrl(mediaDTO.getUrl());
        media.setMediaType(mediaDTO.getMediaType());
        media.setPosition(mediaDTO.getPosition());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        media.setLesson(lesson);
        
        LessonMedia saved = mediaRepository.save(media);
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public LessonMediaDTO updateMedia(Long id, LessonMediaDTO mediaDTO) {
        LessonMedia media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        
        media.setUrl(mediaDTO.getUrl());
        media.setMediaType(mediaDTO.getMediaType());
        media.setPosition(mediaDTO.getPosition());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        
        LessonMedia updated = mediaRepository.save(media);
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void deleteMedia(Long id) {
        mediaRepository.deleteById(id);
    }
    
    @Override
    public LessonMediaDTO getMediaById(Long id) {
        LessonMedia media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        return convertToDTO(media);
    }
    
    @Override
    public List<LessonMediaDTO> getMediaByLesson(Long lessonId) {
        return mediaRepository.findByLessonIdOrderByPositionAsc(lessonId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<LessonMediaDTO> reorderMedia(Long lessonId, List<Long> mediaIds) {
        List<LessonMedia> mediaList = new ArrayList<>();
        
        for (int i = 0; i < mediaIds.size(); i++) {
            Long mediaId = mediaIds.get(i);
            LessonMedia media = mediaRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Media not found: " + mediaId));
            media.setPosition(i);
            mediaList.add(mediaRepository.save(media));
        }
        
        return mediaList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private LessonMediaDTO convertToDTO(LessonMedia media) {
        LessonMediaDTO dto = new LessonMediaDTO();
        dto.setId(media.getId());
        dto.setUrl(media.getUrl());
        dto.setMediaType(media.getMediaType());
        dto.setPosition(media.getPosition());
        dto.setTitle(media.getTitle());
        dto.setDescription(media.getDescription());
        dto.setLessonId(media.getLesson().getId());
        return dto;
    }
}
