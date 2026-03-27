package com.jungle.learning.service;

import com.jungle.learning.model.Question;
import org.springframework.stereotype.Service;

@Service
public class GradingService {
    
    public boolean checkAnswer(Question question, String studentAnswer) {
        if (studentAnswer == null || question.getCorrectAnswer() == null) {
            return false;
        }
        
        String correctAnswer = question.getCorrectAnswer().trim().toLowerCase();
        String answer = studentAnswer.trim().toLowerCase();
        
        return correctAnswer.equals(answer);
    }
    
    public int calculatePoints(Question question, boolean isCorrect) {
        return isCorrect ? question.getPoints() : 0;
    }
}
