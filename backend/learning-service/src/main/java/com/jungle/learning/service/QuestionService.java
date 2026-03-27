package com.jungle.learning.service;

import com.jungle.learning.dto.QuestionDTO;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Question;
import com.jungle.learning.model.Quiz;
import com.jungle.learning.repository.QuestionRepository;
import com.jungle.learning.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    
    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return convertToDTO(question);
    }
    
    @Transactional
    public QuestionDTO createQuestion(QuestionDTO dto) {
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + dto.getQuizId()));
        
        Question question = new Question();
        question.setQuiz(quiz);
        question.setContent(dto.getContent());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints() != null ? dto.getPoints() : 1);
        question.setOrderIndex(dto.getOrderIndex());
        question.setPartialCreditEnabled(dto.getPartialCreditEnabled() != null ? dto.getPartialCreditEnabled() : false);
        
        Question saved = questionRepository.save(question);
        return convertToDTO(saved);
    }
    
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        
        question.setContent(dto.getContent());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints());
        question.setOrderIndex(dto.getOrderIndex());
        question.setPartialCreditEnabled(dto.getPartialCreditEnabled());
        
        Question updated = questionRepository.save(question);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }
    
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz().getId());
        dto.setContent(question.getContent());
        dto.setType(question.getType().name());
        dto.setOptions(question.getOptions());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setPoints(question.getPoints());
        dto.setOrderIndex(question.getOrderIndex());
        dto.setPartialCreditEnabled(question.getPartialCreditEnabled());
        return dto;
    }
}
