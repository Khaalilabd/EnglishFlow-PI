package com.englishflow.exam.service;

import com.englishflow.exam.dto.response.ResultDTO;
import com.englishflow.exam.dto.response.ResultWithReviewDTO;
import com.englishflow.exam.entity.*;
import com.englishflow.exam.enums.ExamLevel;
import com.englishflow.exam.enums.QuestionType;
import com.englishflow.exam.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ExamResultRepository resultRepository;

    @Mock
    private StudentExamAttemptRepository attemptRepository;

    @Mock
    private StudentAnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CorrectAnswerRepository correctAnswerRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ResultService resultService;

    private StudentExamAttempt attempt;
    private ExamResult result;
    private StudentAnswer answer;
    private Question question;
    private CorrectAnswer correctAnswer;
    private Exam exam;
    private ExamPart part;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        exam.setId("exam1");
        exam.setLevel(ExamLevel.B1);
        exam.setPassingScore(70.0);

        part = new ExamPart();
        part.setId("part1");

        attempt = new StudentExamAttempt();
        attempt.setId("attempt1");
        attempt.setUserId(1L);
        attempt.setExam(exam);
        attempt.setTotalScore(80.0);
        attempt.setPercentageScore(80.0);
        attempt.setPassed(true);

        question = new Question();
        question.setId("q1");
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        question.setPoints(10.0);
        question.setPrompt("Test question");
        question.setPart(part);
        question.setExplanation("Test explanation");

        answer = new StudentAnswer();
        answer.setId("ans1");
        answer.setQuestionId("q1");
        answer.setAttempt(attempt);
        answer.setScore(8.0);
        answer.setIsCorrect(true);

        correctAnswer = new CorrectAnswer();
        correctAnswer.setId("ca1");
        correctAnswer.setQuestionId("q1");

        result = ExamResult.builder()
                .id("result1")
                .userId(1L)
                .attemptId("attempt1")
                .level(ExamLevel.B1)
                .totalScore(80.0)
                .percentageScore(80.0)
                .passed(true)
                .cefrBand(ExamLevel.B1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateResult_NewResult_ShouldCreateResult() {
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(resultRepository.save(any(ExamResult.class))).thenReturn(result);

        resultService.generateResult("attempt1");

        verify(resultRepository).save(any(ExamResult.class));
    }

    @Test
    void generateResult_ExistingResult_ShouldNotCreateDuplicate() {
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.of(result));

        resultService.generateResult("attempt1");

        verify(resultRepository, never()).save(any(ExamResult.class));
    }

    @Test
    void generateResult_AttemptNotFound_ShouldThrowException() {
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resultService.generateResult("attempt1"));
    }

    @Test
    void getResultByAttemptId_ValidUser_ShouldReturnResult() {
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.of(result));

        ResultDTO resultDTO = resultService.getResultByAttemptId("attempt1", 1L);

        assertNotNull(resultDTO);
        assertEquals("result1", resultDTO.getId());
        assertEquals(1L, resultDTO.getUserId());
        assertEquals(80.0, resultDTO.getPercentageScore());
    }

    @Test
    void getResultByAttemptId_ResultNotFound_ShouldThrowException() {
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            resultService.getResultByAttemptId("attempt1", 1L)
        );
    }

    @Test
    void getResultByAttemptId_UnauthorizedUser_ShouldThrowException() {
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.of(result));

        assertThrows(RuntimeException.class, () -> 
            resultService.getResultByAttemptId("attempt1", 999L)
        );
    }

    @Test
    void getResultWithReview_ValidUser_ShouldReturnResultWithReviews() {
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.of(result));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(correctAnswerRepository.findByQuestionId("q1")).thenReturn(Optional.of(correctAnswer));

        ResultWithReviewDTO resultWithReview = resultService.getResultWithReview("attempt1", 1L);

        assertNotNull(resultWithReview);
        assertEquals("result1", resultWithReview.getId());
        assertNotNull(resultWithReview.getQuestionReviews());
        assertEquals(1, resultWithReview.getQuestionReviews().size());
    }

    @Test
    void getResultWithReview_UnauthorizedUser_ShouldThrowException() {
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.of(result));

        assertThrows(RuntimeException.class, () -> 
            resultService.getResultWithReview("attempt1", 999L)
        );
    }

    @Test
    void getUserResults_ShouldReturnAllUserResults() {
        ExamResult result2 = ExamResult.builder()
                .id("result2")
                .userId(1L)
                .attemptId("attempt2")
                .level(ExamLevel.B2)
                .totalScore(90.0)
                .percentageScore(90.0)
                .passed(true)
                .cefrBand(ExamLevel.B2)
                .createdAt(LocalDateTime.now())
                .build();

        when(resultRepository.findByUserId(1L)).thenReturn(Arrays.asList(result, result2));

        List<ResultDTO> results = resultService.getUserResults(1L);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("result1", results.get(0).getId());
        assertEquals("result2", results.get(1).getId());
    }

    @Test
    void getUserResults_NoResults_ShouldReturnEmptyList() {
        when(resultRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        List<ResultDTO> results = resultService.getUserResults(1L);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void generateResult_HighScore_ShouldDetermineHigherCEFRBand() {
        attempt.setPercentageScore(95.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(resultRepository.save(any(ExamResult.class))).thenAnswer(invocation -> {
            ExamResult saved = invocation.getArgument(0);
            assertEquals(ExamLevel.B2, saved.getCefrBand());
            return saved;
        });

        resultService.generateResult("attempt1");

        verify(resultRepository).save(any(ExamResult.class));
    }

    @Test
    void generateResult_LowScore_ShouldDetermineLowerCEFRBand() {
        attempt.setPercentageScore(45.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(resultRepository.save(any(ExamResult.class))).thenAnswer(invocation -> {
            ExamResult saved = invocation.getArgument(0);
            assertEquals(ExamLevel.A1, saved.getCefrBand());
            return saved;
        });

        resultService.generateResult("attempt1");

        verify(resultRepository).save(any(ExamResult.class));
    }

    @Test
    void generateResult_MediumScore_ShouldMaintainCEFRBand() {
        attempt.setPercentageScore(75.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(resultRepository.save(any(ExamResult.class))).thenAnswer(invocation -> {
            ExamResult saved = invocation.getArgument(0);
            assertEquals(ExamLevel.B1, saved.getCefrBand());
            return saved;
        });

        resultService.generateResult("attempt1");

        verify(resultRepository).save(any(ExamResult.class));
    }

    @Test
    void generateResult_WithMultipleAnswers_ShouldCalculatePartBreakdown() {
        StudentAnswer answer2 = new StudentAnswer();
        answer2.setId("ans2");
        answer2.setQuestionId("q2");
        answer2.setAttempt(attempt);
        answer2.setScore(9.0);

        Question question2 = new Question();
        question2.setId("q2");
        question2.setQuestionType(QuestionType.TRUE_FALSE);
        question2.setPoints(10.0);
        question2.setPart(part);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(resultRepository.findByAttemptId("attempt1")).thenReturn(Optional.empty());
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer, answer2));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question, question2));
        when(resultRepository.save(any(ExamResult.class))).thenReturn(result);

        resultService.generateResult("attempt1");

        verify(resultRepository).save(any(ExamResult.class));
    }
}
