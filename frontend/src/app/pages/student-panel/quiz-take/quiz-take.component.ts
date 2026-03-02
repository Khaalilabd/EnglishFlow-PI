import { Component, Input, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuizService } from '../../../core/services/quiz.service';
import { AuthService } from '../../../core/services/auth.service';
import { Quiz, Question } from '../../../core/models/quiz.model';

@Component({
  selector: 'app-quiz-take',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-take.component.html',
  styleUrls: ['./quiz-take.component.scss']
})
export class QuizTakeComponent implements OnInit, OnDestroy {
  @Input() quizId!: number;
  @Output() quizCompleted = new EventEmitter<void>();
  
  Math = Math;
  quiz: Quiz | null = null;
  questions: Question[] = [];
  currentQuestionIndex = 0;
  answers: { [questionId: number]: any } = {};
  flaggedQuestions: Set<number> = new Set();
  loading = true;
  submitting = false;
  quizStarted = false;
  quizFinished = false;
  score: number = 0;
  totalQuestions: number = 0;
  
  // Previous attempt tracking
  hasCompletedAttempt = false;
  previousAttempt: any = null;
  
  // Timer
  timeRemaining: number = 0; // in seconds
  timerInterval: any;
  startTime: Date | null = null;

  constructor(
    private quizService: QuizService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadQuiz();
  }

  loadQuiz(): void {
    this.loading = true;
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (quiz) => {
        this.quiz = quiz;
        this.checkPreviousAttempt();
      },
      error: (error) => {
        console.error('Error loading quiz:', error);
        this.loading = false;
      }
    });
  }

  checkPreviousAttempt(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      this.loadQuestions();
      return;
    }

    // Check if student has already completed this quiz
    this.quizService.getStudentAttempts(currentUser.id).subscribe({
      next: (attempts) => {
        const completedAttempt = attempts.find(
          (a: any) => a.quizId === this.quizId && a.status === 'COMPLETED'
        );

        if (completedAttempt && completedAttempt.id) {
          // Student has already completed this quiz
          this.hasCompletedAttempt = true;
          
          // Load the full attempt result
          this.quizService.getAttemptResult(completedAttempt.id).subscribe({
            next: (result) => {
              this.previousAttempt = result;
              this.score = Math.round((result.score / result.maxScore) * 100);
              this.quizFinished = true;
              this.loading = false;
            },
            error: (error) => {
              console.error('Error loading attempt result:', error);
              this.loading = false;
            }
          });
        } else {
          // No completed attempt, allow quiz to be taken
          this.loadQuestions();
        }
      },
      error: (error) => {
        console.error('Error checking previous attempts:', error);
        this.loadQuestions();
      }
    });
  }

  loadQuestions(): void {
    this.quizService.getQuestionsByQuizId(this.quizId).subscribe({
      next: (questions) => {
        this.questions = questions.sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
        this.totalQuestions = questions.length;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading questions:', error);
        this.loading = false;
      }
    });
  }

  startQuiz(): void {
    this.quizStarted = true;
    this.currentQuestionIndex = 0;
    this.startTime = new Date();
    
    // Start timer if quiz has duration
    if (this.quiz?.durationMin) {
      this.timeRemaining = this.quiz.durationMin * 60; // Convert to seconds
      this.startTimer();
    }
  }

  startTimer(): void {
    this.timerInterval = setInterval(() => {
      this.timeRemaining--;
      
      if (this.timeRemaining <= 0) {
        clearInterval(this.timerInterval);
        alert('Time is up! Submitting quiz...');
        this.submitQuiz();
      }
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  toggleFlag(): void {
    const questionId = this.currentQuestion?.id;
    if (!questionId) return;
    
    if (this.flaggedQuestions.has(questionId)) {
      this.flaggedQuestions.delete(questionId);
    } else {
      this.flaggedQuestions.add(questionId);
    }
  }

  isFlagged(questionId: number): boolean {
    return this.flaggedQuestions.has(questionId);
  }

  goToQuestion(index: number): void {
    this.currentQuestionIndex = index;
  }

  getQuestionStatus(index: number): 'answered' | 'flagged' | 'current' | 'unanswered' {
    const question = this.questions[index];
    if (!question.id) return 'unanswered';
    
    if (index === this.currentQuestionIndex) return 'current';
    if (this.answers[question.id] !== undefined) return 'answered';
    if (this.flaggedQuestions.has(question.id)) return 'flagged';
    return 'unanswered';
  }

  cancelQuiz(): void {
    if (confirm('Are you sure you want to cancel this quiz? Your progress will be lost.')) {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
      }
      this.quizCompleted.emit();
    }
  }

  get currentQuestion(): Question | null {
    return this.questions[this.currentQuestionIndex] || null;
  }

  selectAnswer(questionId: number, answer: any): void {
    this.answers[questionId] = answer;
  }

  isAnswerSelected(questionId: number, option: string): boolean {
    return this.answers[questionId] === option;
  }

  nextQuestion(): void {
    if (this.currentQuestionIndex < this.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  canGoNext(): boolean {
    return this.currentQuestionIndex < this.questions.length - 1;
  }

  canGoPrevious(): boolean {
    return this.currentQuestionIndex > 0;
  }

  isCurrentQuestionAnswered(): boolean {
    const question = this.currentQuestion;
    return question ? this.answers[question.id!] !== undefined : false;
  }

  submitQuiz(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !this.quiz) return;

    // Stop timer
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }

    // Calculate score
    let correctAnswers = 0;
    this.questions.forEach(question => {
      const userAnswer = this.answers[question.id!];
      if (userAnswer === question.correctAnswer) {
        correctAnswers++;
      }
    });

    this.score = Math.round((correctAnswers / this.totalQuestions) * 100);
    
    this.submitting = true;

    // Start attempt first, then submit
    this.quizService.startAttempt(this.quizId, currentUser.id).subscribe({
      next: (attempt) => {
        // Submit the attempt with answers
        const attemptRequest = {
          quizId: this.quizId,
          studentId: currentUser.id,
          answers: this.answers
        };

        this.quizService.submitAttempt(attempt.id!, attemptRequest).subscribe({
          next: (result) => {
            this.score = Math.round((result.score / result.maxScore) * 100);
            this.quizFinished = true;
            this.submitting = false;
            
            // Emit completion event after 2 seconds
            setTimeout(() => {
              this.quizCompleted.emit();
            }, 2000);
          },
          error: (error: any) => {
            console.error('Error submitting quiz:', error);
            this.submitting = false;
            alert('Failed to submit quiz. Please try again.');
          }
        });
      },
      error: (error: any) => {
        console.error('Error starting attempt:', error);
        this.submitting = false;
        alert('Failed to start quiz attempt. Please try again.');
      }
    });
  }

  getProgressPercentage(): number {
    if (this.questions.length === 0) return 0;
    return ((this.currentQuestionIndex + 1) / this.questions.length) * 100;
  }

  getAnsweredCount(): number {
    return Object.keys(this.answers).length;
  }
}
