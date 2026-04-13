package com.jungle.learning.service;

import com.jungle.learning.model.Question;
import com.jungle.learning.model.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradingServiceTest {

    private GradingService gradingService;
    private Question question;

    @BeforeEach
    void setUp() {
        gradingService = new GradingService();

        Quiz quiz = new Quiz();
        quiz.setId(1L);

        question = new Question();
        question.setId(1L);
        question.setQuiz(quiz);
        question.setContent("What is 2 + 2?");
        question.setType(Question.QuestionType.MCQ);
        question.setCorrectAnswer("4");
        question.setPoints(10);
    }

    @Test
    void checkAnswer_WithCorrectAnswer_ShouldReturnTrue() {
        // When
        boolean result = gradingService.checkAnswer(question, "4");

        // Then
        assertTrue(result);
    }

    @Test
    void checkAnswer_WithIncorrectAnswer_ShouldReturnFalse() {
        // When
        boolean result = gradingService.checkAnswer(question, "5");

        // Then
        assertFalse(result);
    }

    @Test
    void checkAnswer_WithCorrectAnswerDifferentCase_ShouldReturnTrue() {
        // Given
        question.setCorrectAnswer("Paris");

        // When
        boolean result = gradingService.checkAnswer(question, "PARIS");

        // Then
        assertTrue(result);
    }

    @Test
    void checkAnswer_WithCorrectAnswerExtraSpaces_ShouldReturnTrue() {
        // Given
        question.setCorrectAnswer("Paris");

        // When
        boolean result = gradingService.checkAnswer(question, "  Paris  ");

        // Then
        assertTrue(result);
    }

    @Test
    void checkAnswer_WithNullStudentAnswer_ShouldReturnFalse() {
        // When
        boolean result = gradingService.checkAnswer(question, null);

        // Then
        assertFalse(result);
    }

    @Test
    void checkAnswer_WithNullCorrectAnswer_ShouldReturnFalse() {
        // Given
        question.setCorrectAnswer(null);

        // When
        boolean result = gradingService.checkAnswer(question, "4");

        // Then
        assertFalse(result);
    }

    @Test
    void calculatePoints_WithCorrectAnswer_ShouldReturnFullPoints() {
        // When
        int points = gradingService.calculatePoints(question, true);

        // Then
        assertEquals(10, points);
    }

    @Test
    void calculatePoints_WithIncorrectAnswer_ShouldReturnZeroPoints() {
        // When
        int points = gradingService.calculatePoints(question, false);

        // Then
        assertEquals(0, points);
    }

    @Test
    void calculatePoints_WithDifferentPointValues_ShouldReturnCorrectPoints() {
        // Given
        question.setPoints(25);

        // When
        int correctPoints = gradingService.calculatePoints(question, true);
        int incorrectPoints = gradingService.calculatePoints(question, false);

        // Then
        assertEquals(25, correctPoints);
        assertEquals(0, incorrectPoints);
    }
}
