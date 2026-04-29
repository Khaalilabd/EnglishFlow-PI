package com.englishflow.exam.service;

import com.englishflow.exam.dto.request.ManualGradeDTO;
import com.englishflow.exam.dto.response.GradingQueueItemDTO;
import com.englishflow.exam.entity.*;
import com.englishflow.exam.enums.AttemptStatus;
import com.englishflow.exam.enums.QuestionType;
import com.englishflow.exam.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class GradingServiceTest {

    @Mock
    private StudentExamAttemptRepository attemptRepository;

    @Mock
    private StudentAnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CorrectAnswerRepository correctAnswerRepository;

    @Mock
    private ResultService resultService;

    @InjectMocks
    private GradingService gradingService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private StudentExamAttempt attempt;
    private StudentAnswer answer;
    private Question question;
    private CorrectAnswer correctAnswer;
    private Exam exam;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        exam.setId("exam1");
        exam.setPassingScore(70.0);

        attempt = new StudentExamAttempt();
        attempt.setId("attempt1");
        attempt.setUserId(1L);
        attempt.setExam(exam);
        attempt.setStatus(AttemptStatus.SUBMITTED);

        question = new Question();
        question.setId("q1");
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        question.setPoints(10.0);
        question.setPrompt("What is 2+2?");

        answer = new StudentAnswer();
        answer.setId("ans1");
        answer.setQuestionId("q1");
        answer.setAttempt(attempt);

        correctAnswer = new CorrectAnswer();
        correctAnswer.setId("ca1");
        correctAnswer.setQuestionId("q1");
    }

    @Test
    void gradeAttempt_WithAutoGradableQuestions_ShouldAutoGrade() throws Exception {
        JsonNode studentAnswerData = objectMapper.readTree("\"4\"");
        JsonNode correctAnswerData = objectMapper.readTree("\"4\"");
        
        answer.setAnswerData(studentAnswerData);
        correctAnswer.setAnswerData(correctAnswerData);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(correctAnswerRepository.findByQuestionId("q1")).thenReturn(Optional.of(correctAnswer));
        when(answerRepository.save(any(StudentAnswer.class))).thenReturn(answer);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.gradeAttempt("attempt1");

        verify(answerRepository, atLeastOnce()).save(any(StudentAnswer.class));
        verify(resultService).generateResult("attempt1");
    }

    @Test
    void gradeAttempt_AttemptNotFound_ShouldThrowException() {
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gradingService.gradeAttempt("attempt1"));
    }

    @Test
    void gradeAttempt_WithManualGradingRequired_ShouldNotFinalize() throws Exception {
        question.setQuestionType(QuestionType.OPEN_WRITING);
        answer.setScore(null);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));

        gradingService.gradeAttempt("attempt1");

        verify(resultService, never()).generateResult(anyString());
    }

    @Test
    void getGradingQueue_ShouldReturnUngradedAnswers() {
        answer.setIsCorrect(null);
        
        when(answerRepository.findByIsCorrectIsNull()).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));

        List<GradingQueueItemDTO> queue = gradingService.getGradingQueue();

        assertNotNull(queue);
        assertEquals(1, queue.size());
        assertEquals("ans1", queue.get(0).getAnswerId());
    }

    @Test
    void manualGradeAnswer_ValidGrade_ShouldUpdateAnswer() {
        ManualGradeDTO gradeDTO = new ManualGradeDTO();
        gradeDTO.setScore(8.0);
        gradeDTO.setFeedback("Good answer");

        answer.setScore(null);
        
        when(answerRepository.findById("ans1")).thenReturn(Optional.of(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(answerRepository.save(any(StudentAnswer.class))).thenReturn(answer);
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.manualGradeAnswer("ans1", 100L, gradeDTO);

        verify(answerRepository).save(any(StudentAnswer.class));
        verify(resultService).generateResult("attempt1");
    }

    @Test
    void manualGradeAnswer_AnswerNotFound_ShouldThrowException() {
        ManualGradeDTO gradeDTO = new ManualGradeDTO();
        gradeDTO.setScore(8.0);

        when(answerRepository.findById("ans1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            gradingService.manualGradeAnswer("ans1", 100L, gradeDTO)
        );
    }

    @Test
    void finalizeAttemptGrading_ShouldCalculateScoresAndGenerateResult() {
        answer.setScore(8.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.finalizeAttemptGrading("attempt1");

        verify(attemptRepository).save(any(StudentExamAttempt.class));
        verify(resultService).generateResult("attempt1");
    }

    @Test
    void finalizeAttemptGrading_HighScore_ShouldMarkAsPassed() {
        answer.setScore(10.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenAnswer(invocation -> {
            StudentExamAttempt saved = invocation.getArgument(0);
            assertTrue(saved.getPassed());
            assertEquals(100.0, saved.getPercentageScore());
            return saved;
        });

        gradingService.finalizeAttemptGrading("attempt1");

        verify(attemptRepository).save(any(StudentExamAttempt.class));
    }

    @Test
    void finalizeAttemptGrading_LowScore_ShouldMarkAsFailed() {
        answer.setScore(3.0);
        
        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findAllById(any())).thenReturn(Arrays.asList(question));
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenAnswer(invocation -> {
            StudentExamAttempt saved = invocation.getArgument(0);
            assertFalse(saved.getPassed());
            assertEquals(30.0, saved.getPercentageScore());
            return saved;
        });

        gradingService.finalizeAttemptGrading("attempt1");

        verify(attemptRepository).save(any(StudentExamAttempt.class));
    }

    @Test
    void gradeAttempt_TrueFalseQuestion_ShouldAutoGrade() throws Exception {
        question.setQuestionType(QuestionType.TRUE_FALSE);
        JsonNode studentAnswerData = objectMapper.readTree("true");
        JsonNode correctAnswerData = objectMapper.readTree("true");
        
        answer.setAnswerData(studentAnswerData);
        correctAnswer.setAnswerData(correctAnswerData);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(correctAnswerRepository.findByQuestionId("q1")).thenReturn(Optional.of(correctAnswer));
        when(answerRepository.save(any(StudentAnswer.class))).thenReturn(answer);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.gradeAttempt("attempt1");

        verify(answerRepository, atLeastOnce()).save(any(StudentAnswer.class));
    }

    @Test
    void gradeAttempt_FillInGapQuestion_ShouldAutoGrade() throws Exception {
        question.setQuestionType(QuestionType.FILL_IN_GAP);
        JsonNode studentAnswerData = objectMapper.readTree("\"answer\"");
        JsonNode correctAnswerData = objectMapper.readTree("\"answer\"");
        
        answer.setAnswerData(studentAnswerData);
        correctAnswer.setAnswerData(correctAnswerData);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(correctAnswerRepository.findByQuestionId("q1")).thenReturn(Optional.of(correctAnswer));
        when(answerRepository.save(any(StudentAnswer.class))).thenReturn(answer);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.gradeAttempt("attempt1");

        verify(answerRepository, atLeastOnce()).save(any(StudentAnswer.class));
    }

    @Test
    void gradeAttempt_MatchingQuestion_ShouldAutoGradePartially() throws Exception {
        question.setQuestionType(QuestionType.MATCHING);
        JsonNode studentAnswerData = objectMapper.readTree("{\"a\":\"1\",\"b\":\"2\"}");
        JsonNode correctAnswerData = objectMapper.readTree("{\"a\":\"1\",\"b\":\"3\"}");
        
        answer.setAnswerData(studentAnswerData);
        correctAnswer.setAnswerData(correctAnswerData);

        when(attemptRepository.findById("attempt1")).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttemptId("attempt1")).thenReturn(Arrays.asList(answer));
        when(questionRepository.findById("q1")).thenReturn(Optional.of(question));
        when(correctAnswerRepository.findByQuestionId("q1")).thenReturn(Optional.of(correctAnswer));
        when(answerRepository.save(any(StudentAnswer.class))).thenReturn(answer);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(attempt);

        gradingService.gradeAttempt("attempt1");

        verify(answerRepository, atLeastOnce()).save(any(StudentAnswer.class));
    }
}
