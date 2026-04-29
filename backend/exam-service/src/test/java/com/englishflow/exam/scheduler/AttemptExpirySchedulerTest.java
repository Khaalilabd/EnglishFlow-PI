package com.englishflow.exam.scheduler;

import com.englishflow.exam.entity.Exam;
import com.englishflow.exam.entity.StudentExamAttempt;
import com.englishflow.exam.enums.AttemptStatus;
import com.englishflow.exam.repository.StudentExamAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptExpirySchedulerTest {

    @Mock
    private StudentExamAttemptRepository attemptRepository;

    @InjectMocks
    private AttemptExpiryScheduler scheduler;

    private StudentExamAttempt expiredAttempt;
    private StudentExamAttempt activeAttempt;
    private Exam exam;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        exam.setId("exam1");
        exam.setTotalDuration(120); // 120 minutes

        // Attempt that should be expired (started 3 hours ago)
        expiredAttempt = new StudentExamAttempt();
        expiredAttempt.setId("attempt1");
        expiredAttempt.setUserId(1L);
        expiredAttempt.setExam(exam);
        expiredAttempt.setStatus(AttemptStatus.STARTED);
        expiredAttempt.setStartedAt(LocalDateTime.now().minusHours(3));

        // Attempt that is still active (started 30 minutes ago)
        activeAttempt = new StudentExamAttempt();
        activeAttempt.setId("attempt2");
        activeAttempt.setUserId(2L);
        activeAttempt.setExam(exam);
        activeAttempt.setStatus(AttemptStatus.STARTED);
        activeAttempt.setStartedAt(LocalDateTime.now().minusMinutes(30));
    }

    @Test
    void expireTimedOutAttempts_WithExpiredAttempts_ShouldExpireThem() {
        List<StudentExamAttempt> attempts = Arrays.asList(expiredAttempt, activeAttempt);
        when(attemptRepository.findByStatus(AttemptStatus.STARTED)).thenReturn(attempts);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(expiredAttempt);

        scheduler.expireTimedOutAttempts();

        verify(attemptRepository).findByStatus(AttemptStatus.STARTED);
        verify(attemptRepository, times(1)).save(any(StudentExamAttempt.class));
    }

    @Test
    void expireTimedOutAttempts_WithNoExpiredAttempts_ShouldNotExpireAny() {
        List<StudentExamAttempt> attempts = Arrays.asList(activeAttempt);
        when(attemptRepository.findByStatus(AttemptStatus.STARTED)).thenReturn(attempts);

        scheduler.expireTimedOutAttempts();

        verify(attemptRepository).findByStatus(AttemptStatus.STARTED);
        verify(attemptRepository, never()).save(any(StudentExamAttempt.class));
    }

    @Test
    void expireTimedOutAttempts_WithNoStartedAttempts_ShouldDoNothing() {
        when(attemptRepository.findByStatus(AttemptStatus.STARTED)).thenReturn(Arrays.asList());

        scheduler.expireTimedOutAttempts();

        verify(attemptRepository).findByStatus(AttemptStatus.STARTED);
        verify(attemptRepository, never()).save(any(StudentExamAttempt.class));
    }

    @Test
    void expireTimedOutAttempts_WithMultipleExpiredAttempts_ShouldExpireAll() {
        StudentExamAttempt expiredAttempt2 = new StudentExamAttempt();
        expiredAttempt2.setId("attempt3");
        expiredAttempt2.setUserId(3L);
        expiredAttempt2.setExam(exam);
        expiredAttempt2.setStatus(AttemptStatus.STARTED);
        expiredAttempt2.setStartedAt(LocalDateTime.now().minusHours(4));

        List<StudentExamAttempt> attempts = Arrays.asList(expiredAttempt, expiredAttempt2);
        when(attemptRepository.findByStatus(AttemptStatus.STARTED)).thenReturn(attempts);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(expiredAttempt);

        scheduler.expireTimedOutAttempts();

        verify(attemptRepository).findByStatus(AttemptStatus.STARTED);
        verify(attemptRepository, times(2)).save(any(StudentExamAttempt.class));
    }

    @Test
    void expireTimedOutAttempts_AtExactExpiryTime_ShouldExpire() {
        // Attempt that started exactly 120 minutes ago (at expiry boundary)
        StudentExamAttempt boundaryAttempt = new StudentExamAttempt();
        boundaryAttempt.setId("attempt4");
        boundaryAttempt.setUserId(4L);
        boundaryAttempt.setExam(exam);
        boundaryAttempt.setStatus(AttemptStatus.STARTED);
        boundaryAttempt.setStartedAt(LocalDateTime.now().minusMinutes(121)); // Just past expiry

        List<StudentExamAttempt> attempts = Arrays.asList(boundaryAttempt);
        when(attemptRepository.findByStatus(AttemptStatus.STARTED)).thenReturn(attempts);
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(boundaryAttempt);

        scheduler.expireTimedOutAttempts();

        verify(attemptRepository).save(any(StudentExamAttempt.class));
    }
}
