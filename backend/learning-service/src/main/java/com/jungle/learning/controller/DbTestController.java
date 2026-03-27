package com.jungle.learning.controller;

import com.jungle.learning.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dbtest")
@RequiredArgsConstructor
public class DbTestController {
    
    private final QuizRepository quizRepository;
    
    @GetMapping("/count")
    public Map<String, Object> getQuizCount() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = quizRepository.count();
            response.put("status", "success");
            response.put("quizCount", count);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("cause", e.getCause() != null ? e.getCause().getMessage() : "No cause");
        }
        return response;
    }
}
