package com.englishflow.courses.controller;

import com.englishflow.courses.dto.LessonMediaDTO;
import com.englishflow.courses.service.ILessonMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lesson-media")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LessonMediaController {
    
    private final ILessonMediaService mediaService;
    
    @PostMapping
    public ResponseEntity<LessonMediaDTO> createMedia(@RequestBody LessonMediaDTO mediaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.createMedia(mediaDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LessonMediaDTO> updateMedia(@PathVariable Long id, @RequestBody LessonMediaDTO mediaDTO) {
        return ResponseEntity.ok(mediaService.updateMedia(id, mediaDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LessonMediaDTO> getMediaById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMediaById(id));
    }
    
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<LessonMediaDTO>> getMediaByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(mediaService.getMediaByLesson(lessonId));
    }
    
    @PutMapping("/lesson/{lessonId}/reorder")
    public ResponseEntity<List<LessonMediaDTO>> reorderMedia(
            @PathVariable Long lessonId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> mediaIds = request.get("mediaIds");
        return ResponseEntity.ok(mediaService.reorderMedia(lessonId, mediaIds));
    }
}
