package com.jungle.learning.service;

import com.jungle.learning.dto.AttemptRequestDTO;
import com.jungle.learning.dto.AttemptResultDTO;
import com.jungle.learning.dto.QuizAttemptDTO;
import com.jungle.learning.exception.InvalidQuizAttemptException;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Question;
import com.jungle.learning.model.Quiz;
import com.jungle.learning.model.QuizAttempt;
import com.jungle.learning.model.StudentAnswer;
import com.jungle.learning.repository.QuestionRepository;
import com.jungle.learning.repository.QuizAttemptRepository;
import com.jungle.learning.repository.QuizRepository;
import com.jungle.learning.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {
    
    private final QuizAttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final GradingService gradingService;
    
    @Transactional
    public QuizAttemptDTO startAttempt(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        
        if (!quiz.getPublished()) {
            throw new InvalidQuizAttemptException("Quiz is not published yet");
        }
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudentId(studentId);
        attempt.setStatus(QuizAttempt.AttemptStatus.IN_PROGRESS);
        
        QuizAttempt saved = attemptRepository.save(attempt);
        return convertToDTO(saved);
    }
    
    public List<QuizAttemptDTO> getStudentAttempts(Long studentId) {
        return attemptRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private QuizAttemptDTO convertToDTO(QuizAttempt attempt) {
        QuizAttemptDTO dto = new QuizAttemptDTO();
        dto.setId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setStudentId(attempt.getStudentId());
        dto.setScore(attempt.getScore());
        dto.setStartedAt(attempt.getStartedAt());
        dto.setSubmittedAt(attempt.getSubmittedAt());
        dto.setStatus(attempt.getStatus().name());
        return dto;
    }
    
    @Transactional
    public AttemptResultDTO submitAttempt(Long attemptId, AttemptRequestDTO request) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        
        if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
            throw new InvalidQuizAttemptException("Attempt already submitted");
        }
        
        Quiz quiz = attempt.getQuiz();
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndexAsc(quiz.getId());
        
        int totalScore = 0;
        Map<Long, AttemptResultDTO.AnswerDetail> answerDetails = new HashMap<>();
        
        for (Question question : questions) {
            String studentAnswer = request.getAnswers().get(question.getId());
            boolean isCorrect = gradingService.checkAnswer(question, studentAnswer);
            int pointsEarned = gradingService.calculatePoints(question, isCorrect);
            
            StudentAnswer answer = new StudentAnswer();
            answer.setAttempt(attempt);
            answer.setQuestionId(question.getId());
            answer.setAnswer(studentAnswer);
            answer.setIsCorrect(isCorrect);
            answer.setPointsEarned(pointsEarned);
            studentAnswerRepository.save(answer);
            
            totalScore += pointsEarned;
            
            AttemptResultDTO.AnswerDetail detail = new AttemptResultDTO.AnswerDetail();
            detail.setStudentAnswer(studentAnswer);
            detail.setCorrectAnswer(question.getCorrectAnswer());
            detail.setIsCorrect(isCorrect);
            detail.setPointsEarned(pointsEarned);
            answerDetails.put(question.getId(), detail);
        }
        
        attempt.setScore(totalScore);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);
        attemptRepository.save(attempt);
        
        AttemptResultDTO result = new AttemptResultDTO();
        result.setAttemptId(attempt.getId());
        result.setQuizId(quiz.getId());
        result.setStudentId(attempt.getStudentId());
        result.setScore(totalScore);
        result.setMaxScore(quiz.getMaxScore());
        result.setPassed(totalScore >= quiz.getPassingScore());
        result.setStartedAt(attempt.getStartedAt());
        result.setSubmittedAt(attempt.getSubmittedAt());
        result.setStatus(attempt.getStatus().name());
        result.setAnswerDetails(answerDetails);
        
        return result;
    }
    
    public AttemptResultDTO getAttemptResult(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        
        Quiz quiz = attempt.getQuiz();
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);
        
        Map<Long, AttemptResultDTO.AnswerDetail> answerDetails = new HashMap<>();
        for (StudentAnswer answer : answers) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElse(null);
            
            AttemptResultDTO.AnswerDetail detail = new AttemptResultDTO.AnswerDetail();
            detail.setStudentAnswer(answer.getAnswer());
            detail.setCorrectAnswer(question != null ? question.getCorrectAnswer() : null);
            detail.setIsCorrect(answer.getIsCorrect());
            detail.setPointsEarned(answer.getPointsEarned());
            answerDetails.put(answer.getQuestionId(), detail);
        }
        
        AttemptResultDTO result = new AttemptResultDTO();
        result.setAttemptId(attempt.getId());
        result.setQuizId(quiz.getId());
        result.setStudentId(attempt.getStudentId());
        result.setScore(attempt.getScore());
        result.setMaxScore(quiz.getMaxScore());
        result.setPassed(attempt.getScore() >= quiz.getPassingScore());
        result.setStartedAt(attempt.getStartedAt());
        result.setSubmittedAt(attempt.getSubmittedAt());
        result.setStatus(attempt.getStatus().name());
        result.setAnswerDetails(answerDetails);
        
        return result;
    }
}
