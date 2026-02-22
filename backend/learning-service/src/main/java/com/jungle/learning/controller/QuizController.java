package com.jungle.learning.controller;

import com.jungle.learning.dto.QuizDTO;
import com.jungle.learning.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learning/quizzes")
public class QuizController {
    
    private final QuizService quizService;
    
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    
    @GetMapping("/simple")
    public ResponseEntity<?> getSimpleTest() {
        try {
            // Return empty list without touching database
            return ResponseEntity.ok(new java.util.ArrayList<>());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        System.out.println("=== getAllQuizzes called ===");
        try {
            System.out.println("Calling quizService.getAllQuizzes()");
            List<QuizDTO> quizzes = quizService.getAllQuizzes();
            System.out.println("Successfully retrieved " + quizzes.size() + " quizzes");
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            System.err.println("ERROR in getAllQuizzes: " + e.getClass().getName());
            System.err.println("ERROR message: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("type", e.getClass().getName());
            error.put("cause", e.getCause() != null ? e.getCause().getMessage() : "No cause");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<QuizDTO>> getPublishedQuizzes() {
        return ResponseEntity.ok(quizService.getPublishedQuizzes());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(quizService.getQuizzesByCategory(category));
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(quizService.getQuizzesByDifficulty(difficulty));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }
    
    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @RequestBody QuizDTO dto) {
        return ResponseEntity.ok(quizService.updateQuiz(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
