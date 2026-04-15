package com.englishflow.courses.controller;

import com.englishflow.courses.dto.OnlineLessonConfigDTO;
import com.englishflow.courses.dto.LessonSessionDTO;
import com.englishflow.courses.entity.OnlineLesson;
import com.englishflow.courses.service.OnlineLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/online-lessons")
@RequiredArgsConstructor
public class OnlineLessonController {
    
    private final OnlineLessonService onlineLessonService;
    
    @PostMapping("/configure")
    public ResponseEntity<OnlineLesson> configureOnlineLesson(@RequestBody OnlineLessonConfigDTO config) {
        OnlineLesson lesson = onlineLessonService.configureOnlineLesson(config);
        return ResponseEntity.ok(lesson);
    }
    
    @PostMapping("/generate-sessions")
    public ResponseEntity<String> generateSessions(@RequestParam(defaultValue = "4") int weeks) {
        onlineLessonService.generateSessionsForNextWeeks(weeks);
        return ResponseEntity.ok("Sessions generated for next " + weeks + " weeks");
    }
    
    @GetMapping("/student/{studentId}/upcoming")
    public ResponseEntity<List<LessonSessionDTO>> getUpcomingSessionsForStudent(@PathVariable Long studentId) {
        List<LessonSessionDTO> sessions = onlineLessonService.getUpcomingSessionsForStudent(studentId);
        return ResponseEntity.ok(sessions);
    }
    
    @PostMapping("/sessions/{sessionId}/attendance")
    public ResponseEntity<String> recordAttendance(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestParam String joinTime,
            @RequestParam String leaveTime) {
        
        onlineLessonService.recordAttendance(
            sessionId,
            studentId,
            LocalTime.parse(joinTime),
            LocalTime.parse(leaveTime)
        );
        
        return ResponseEntity.ok("Attendance recorded");
    }
    
    @PostMapping("/{lessonId}/assign-time-slot")
    public ResponseEntity<?> assignTimeSlot(
            @PathVariable Long lessonId,
            @RequestParam Long tutorId,
            @RequestBody com.englishflow.courses.dto.AssignTimeSlotRequest request) {
        try {
            System.out.println("=== ASSIGN TIME SLOT DEBUG ===");
            System.out.println("Lesson ID: " + lessonId);
            System.out.println("Tutor ID: " + tutorId);
            System.out.println("Request: " + request);
            System.out.println("Day: " + request.getDayOfWeek());
            System.out.println("Start: " + request.getStartTime());
            System.out.println("End: " + request.getEndTime());
            
            java.time.LocalTime startTime = java.time.LocalTime.parse(request.getStartTime());
            java.time.LocalTime endTime = java.time.LocalTime.parse(request.getEndTime());
            
            System.out.println("Parsed times - Start: " + startTime + ", End: " + endTime);
            
            com.englishflow.courses.entity.LessonTimeAssignment assignment = 
                onlineLessonService.assignTimeSlot(lessonId, tutorId, request.getDayOfWeek(), startTime, endTime);
            
            System.out.println("Assignment created successfully: " + assignment.getId());
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            System.err.println("ERROR in assignTimeSlot: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{lessonId}/time-assignment")
    public ResponseEntity<?> getTimeAssignment(@PathVariable Long lessonId) {
        com.englishflow.courses.entity.LessonTimeAssignment assignment = 
            onlineLessonService.getTimeAssignment(lessonId);
        
        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(assignment);
    }
    
    @DeleteMapping("/{lessonId}/time-assignment")
    public ResponseEntity<String> removeTimeAssignment(@PathVariable Long lessonId) {
        onlineLessonService.removeTimeAssignment(lessonId);
        return ResponseEntity.ok("Time assignment removed");
    }
    
    @GetMapping("/tutor/{tutorId}/scheduled")
    public ResponseEntity<List<com.englishflow.courses.dto.TutorOnlineLessonDTO>> getTutorScheduledLessons(@PathVariable Long tutorId) {
        List<com.englishflow.courses.dto.TutorOnlineLessonDTO> lessons = 
            onlineLessonService.getTutorScheduledLessons(tutorId);
        return ResponseEntity.ok(lessons);
    }
}
