package com.jungle.learning.controller;

import com.jungle.learning.dto.AttemptRequestDTO;
import com.jungle.learning.dto.AttemptResultDTO;
import com.jungle.learning.dto.QuizAttemptDTO;
import com.jungle.learning.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning/attempts")
@RequiredArgsConstructor
public class AttemptController {
    
    private final QuizAttemptService attemptService;
    
    @PostMapping("/start")
    public ResponseEntity<QuizAttemptDTO> startAttempt(
            @RequestParam Long quizId,
            @RequestParam Long studentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attemptService.startAttempt(quizId, studentId));
    }
    
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AttemptResultDTO> submitAttempt(
            @PathVariable Long attemptId,
            @RequestBody AttemptRequestDTO request) {
        return ResponseEntity.ok(attemptService.submitAttempt(attemptId, request));
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<QuizAttemptDTO>> getStudentAttempts(@PathVariable Long studentId) {
        return ResponseEntity.ok(attemptService.getStudentAttempts(studentId));
    }
    
    @GetMapping("/{attemptId}/result")
    public ResponseEntity<AttemptResultDTO> getAttemptResult(@PathVariable Long attemptId) {
        return ResponseEntity.ok(attemptService.getAttemptResult(attemptId));
    }
}
