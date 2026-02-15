package com.jungle.learning.service;

import com.jungle.learning.dto.QuestionDTO;
import com.jungle.learning.dto.QuizDTO;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Question;
import com.jungle.learning.model.Quiz;
import com.jungle.learning.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    
    private final QuizRepository quizRepository;
    
    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzes() {
        try {
            List<Quiz> quizzes = quizRepository.findAll();
            return quizzes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAllQuizzes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching quizzes", e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<QuizDTO> getPublishedQuizzes() {
        return quizRepository.findByPublished(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByCategory(String category) {
        return quizRepository.findByCategoryAndPublishedTrue(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByDifficulty(String difficulty) {
        return quizRepository.findByDifficultyAndPublishedTrue(difficulty)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return convertToDTO(quiz);
    }
    
    @Transactional
    public QuizDTO createQuiz(QuizDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCourseId(dto.getCourseId());
        quiz.setDurationMinutes(dto.getDurationMin());
        quiz.setMaxScore(dto.getMaxScore());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setPublished(dto.getPublished() != null ? dto.getPublished() : false);
        quiz.setDueDate(dto.getDueDate());
        
        // New advanced features
        quiz.setPublishAt(dto.getPublishAt());
        quiz.setShuffleQuestions(dto.getShuffleQuestions() != null ? dto.getShuffleQuestions() : false);
        quiz.setShuffleOptions(dto.getShuffleOptions() != null ? dto.getShuffleOptions() : false);
        quiz.setShowAnswersTiming(dto.getShowAnswersTiming() != null ? dto.getShowAnswersTiming() : "end");
        quiz.setCategory(dto.getCategory());
        quiz.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : "medium");
        quiz.setTags(dto.getTags());
        
        Quiz saved = quizRepository.save(quiz);
        return convertToDTO(saved);
    }
    
    @Transactional
    public QuizDTO updateQuiz(Long id, QuizDTO dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCourseId(dto.getCourseId());
        quiz.setDurationMinutes(dto.getDurationMin());
        quiz.setMaxScore(dto.getMaxScore());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setPublished(dto.getPublished());
        quiz.setDueDate(dto.getDueDate());
        
        // New advanced features
        quiz.setPublishAt(dto.getPublishAt());
        quiz.setShuffleQuestions(dto.getShuffleQuestions());
        quiz.setShuffleOptions(dto.getShuffleOptions());
        quiz.setShowAnswersTiming(dto.getShowAnswersTiming());
        quiz.setCategory(dto.getCategory());
        quiz.setDifficulty(dto.getDifficulty());
        quiz.setTags(dto.getTags());
        
        Quiz updated = quizRepository.save(quiz);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
    
    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCourseId(quiz.getCourseId());
        dto.setDurationMin(quiz.getDurationMinutes());
        dto.setMaxScore(quiz.getMaxScore());
        dto.setPassingScore(quiz.getPassingScore());
        dto.setPublished(quiz.getPublished());
        dto.setDueDate(quiz.getDueDate());
        dto.setCreatedAt(quiz.getCreatedAt());
        dto.setUpdatedAt(quiz.getUpdatedAt());
        
        // New advanced features
        dto.setPublishAt(quiz.getPublishAt());
        dto.setShuffleQuestions(quiz.getShuffleQuestions());
        dto.setShuffleOptions(quiz.getShuffleOptions());
        dto.setShowAnswersTiming(quiz.getShowAnswersTiming());
        dto.setCategory(quiz.getCategory());
        dto.setDifficulty(quiz.getDifficulty());
        dto.setTags(quiz.getTags());
        
        // Don't load questions in list view to avoid lazy loading issues
        // Questions should be loaded separately via /api/questions/quiz/{id}
        
        return dto;
    }
    
    private QuestionDTO convertQuestionToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz().getId());
        dto.setContent(question.getContent());
        dto.setType(question.getType().name());
        dto.setOptions(question.getOptions());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setPoints(question.getPoints());
        dto.setOrderIndex(question.getOrderIndex());
        return dto;
    }
}
