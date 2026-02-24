package com.englishflow.courses.service;

import com.englishflow.courses.dto.LessonMediaDTO;

import java.util.List;

public interface ILessonMediaService {
    LessonMediaDTO createMedia(LessonMediaDTO mediaDTO);
    LessonMediaDTO updateMedia(Long id, LessonMediaDTO mediaDTO);
    void deleteMedia(Long id);
    LessonMediaDTO getMediaById(Long id);
    List<LessonMediaDTO> getMediaByLesson(Long lessonId);
    List<LessonMediaDTO> reorderMedia(Long lessonId, List<Long> mediaIds);
}
