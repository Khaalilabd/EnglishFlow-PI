import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuizService } from '../../../core/services/quiz.service';
import { Quiz, Question } from '../../../core/models/quiz.model';

@Component({
  selector: 'app-quizzes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quizzes.component.html'
})
export class QuizzesComponent implements OnInit {
  Math = Math;
  Object = Object;
  
  quizzes: Quiz[] = [];
  selectedQuiz: Quiz | null = null;
  questions: Question[] = [];
  isLoading = false;
  showQuizModal = false;
  showResultsModal = false;
  showHistoryModal = false;
  
  // History
  myAttempts: any[] = [];
  selectedAttempt: any = null;
  
  // Filtering
  filterCategory: string = '';
  filterDifficulty: string = '';
  filterTag: string = '';
  showFilters: boolean = false;
  
  // View mode
  viewMode: 'published' | 'scheduled' = 'published';
  
  // Track question counts for each quiz
  quizQuestionCounts: { [quizId: number]: number } = {};
  
  // Quiz taking state
  currentQuestionIndex = 0;
  currentAnswers: { [questionId: number]: string } = {};
  attemptId: number | null = null;
  quizStartTime: Date | null = null;
  timeRemaining: number = 0;
  timerInterval: any;
  quizResult: any = null;
  
  // Gamification features
  currentStreak = 0;
  maxStreak = 0;
  streakMultiplier = 1;
  timeBanked = 0;
  flaggedQuestions: Set<number> = new Set();
  questionStartTime: Date | null = null;
  questionTimings: { [questionId: number]: number } = {}; // seconds spent per question
  showStreakAnimation = false;
  streakMessage = '';

  constructor(private quizService: QuizService) {}

  ngOnInit() {
    this.loadQuizzes();
    this.loadMyAttempts();
  }

  loadQuizzes() {
    this.isLoading = true;
    // Load only PUBLISHED and SCHEDULED quizzes for students
    this.quizService.getAllQuizzes().subscribe({
      next: (data) => {
        const now = new Date();
        // Filter to show only published (and not scheduled for future) OR scheduled quizzes
        this.quizzes = data.filter(quiz => {
          // Show if published and not scheduled for future
          const isPublishedNow = quiz.published && (!quiz.publishAt || new Date(quiz.publishAt) <= now);
          // Or if scheduled for future (regardless of published status)
          const isScheduled = quiz.publishAt && new Date(quiz.publishAt) > now;
          return isPublishedNow || isScheduled;
        });
        // Load question counts for each quiz
        this.quizzes.forEach(quiz => {
          if (quiz.id) {
            this.loadQuestionCount(quiz.id);
          }
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading quizzes:', error);
        this.isLoading = false;
      }
    });
  }

  loadQuestionCount(quizId: number) {
    this.quizService.getQuestionsByQuizId(quizId).subscribe({
      next: (questions) => {
        this.quizQuestionCounts[quizId] = questions.length;
      },
      error: (error) => {
        console.error('Error loading question count:', error);
        this.quizQuestionCounts[quizId] = 0;
      }
    });
  }

  getQuestionCount(quizId: number): number {
    return this.quizQuestionCounts[quizId] || 0;
  }

  hasQuestions(quizId: number): boolean {
    return this.getQuestionCount(quizId) > 0;
  }

  startQuiz(quiz: Quiz) {
    this.selectedQuiz = quiz;
    this.currentQuestionIndex = 0;
    this.currentAnswers = {};
    this.quizResult = null;
    
    // Reset gamification state
    this.currentStreak = 0;
    this.maxStreak = 0;
    this.streakMultiplier = 1;
    this.timeBanked = 0;
    this.flaggedQuestions.clear();
    this.questionTimings = {};
    
    if (quiz.id) {
      this.isLoading = true;
      this.quizService.getQuestionsByQuizId(quiz.id).subscribe({
        next: (questions) => {
          if (questions.length === 0) {
            alert('This quiz has no questions yet. Please add questions first.');
            this.isLoading = false;
            return;
          }
          
          // Apply question shuffling if enabled
          if (quiz.shuffleQuestions) {
            this.questions = this.shuffleArray([...questions]);
          } else {
            this.questions = questions;
          }
          
          // Apply option shuffling if enabled
          if (quiz.shuffleOptions) {
            this.questions = this.questions.map(q => {
              if (q.type === 'MCQ' && q.options) {
                const optionsArray = q.options.split(',');
                const shuffledOptions = this.shuffleArray([...optionsArray]);
                return { ...q, options: shuffledOptions.join(',') };
              }
              return q;
            });
          }
          
          // Start attempt
          this.quizService.startAttempt(quiz.id!, 1).subscribe({
            next: (attempt) => {
              this.attemptId = attempt.id!;
              this.quizStartTime = new Date();
              this.timeRemaining = (quiz.durationMin || 30) * 60; // Convert to seconds
              this.startTimer();
              this.startQuestionTimer(); // Start timing first question
              this.showQuizModal = true;
              this.isLoading = false;
            },
            error: (error) => {
              console.error('Error starting quiz attempt:', error);
              console.error('Error details:', error.error);
              let errorMessage = 'Error starting quiz attempt. ';
              if (error.error?.message) {
                errorMessage += error.error.message;
              } else if (error.message) {
                errorMessage += error.message;
              } else {
                errorMessage += 'Please try again.';
              }
              alert(errorMessage);
              this.isLoading = false;
            }
          });
        },
        error: (error) => {
          console.error('Error loading questions:', error);
          alert('Error loading questions. Please try again.');
          this.isLoading = false;
        }
      });
    }
  }
  
  // Fisher-Yates shuffle algorithm
  shuffleArray<T>(array: T[]): T[] {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
  }

  startTimer() {
    this.timerInterval = setInterval(() => {
      this.timeRemaining--;
      if (this.timeRemaining <= 0) {
        this.submitQuiz();
      }
    }, 1000);
  }

  stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  getTimeDisplay(): string {
    const minutes = Math.floor(this.timeRemaining / 60);
    const seconds = this.timeRemaining % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  getProgressPercentage(): number {
    return ((this.currentQuestionIndex + 1) / this.questions.length) * 100;
  }

  nextQuestion() {
    if (this.currentQuestionIndex < this.questions.length - 1) {
      this.endQuestionTimer(); // End timing for current question
      this.currentQuestionIndex++;
      this.startQuestionTimer(); // Start timing for next question
    }
  }

  previousQuestion() {
    if (this.currentQuestionIndex > 0) {
      this.endQuestionTimer();
      this.currentQuestionIndex--;
      this.startQuestionTimer();
    }
  }

  goToQuestion(index: number) {
    this.endQuestionTimer();
    this.currentQuestionIndex = index;
    this.startQuestionTimer();
  }

  isQuestionAnswered(questionId: number): boolean {
    return !!this.currentAnswers[questionId];
  }

  submitQuiz() {
    this.stopTimer();
    
    if (!this.attemptId || !this.selectedQuiz) return;

    const request = {
      quizId: this.selectedQuiz.id!,
      studentId: 1,
      answers: this.currentAnswers
    };

    this.isLoading = true;
    this.quizService.submitAttempt(this.attemptId, request).subscribe({
      next: (result) => {
        this.quizResult = result;
        this.showQuizModal = false;
        this.showResultsModal = true;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error submitting quiz:', error);
        alert('Error submitting quiz');
        this.isLoading = false;
      }
    });
  }

  closeResultsModal() {
    this.showResultsModal = false;
    this.quizResult = null;
    this.selectedQuiz = null;
    this.questions = [];
    this.currentAnswers = {};
    this.attemptId = null;
    this.loadQuizzes();
    this.loadMyAttempts();
  }

  loadMyAttempts() {
    this.quizService.getStudentAttempts(1).subscribe({
      next: (attempts) => {
        this.myAttempts = attempts.filter((a: any) => a.status === 'COMPLETED');
      },
      error: (error) => console.error('Error loading attempts:', error)
    });
  }

  openHistoryModal() {
    this.loadMyAttempts();
    this.showHistoryModal = true;
  }

  closeHistoryModal() {
    this.showHistoryModal = false;
    this.selectedAttempt = null;
  }

  viewAttemptDetails(attemptId: number) {
    this.quizService.getAttemptResult(attemptId).subscribe({
      next: (result) => {
        this.selectedAttempt = result;
      },
      error: (error) => console.error('Error loading attempt details:', error)
    });
  }

  getQuizTitle(quizId: number): string {
    const quiz = this.quizzes.find(q => q.id === quizId);
    return quiz ? quiz.title : 'Unknown Quiz';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  closeQuizModal() {
    this.stopTimer();
    this.showQuizModal = false;
    this.selectedQuiz = null;
    this.questions = [];
    this.currentAnswers = {};
    this.attemptId = null;
    this.currentQuestionIndex = 0;
  }

  ngOnDestroy() {
    this.stopTimer();
  }

  getOptionsArray(options: string | undefined): string[] {
    return options ? options.split(',') : [];
  }
  
  // Check if answers should be shown based on quiz settings
  shouldShowAnswers(quiz: Quiz, attemptCompleted: boolean = false): boolean {
    if (!quiz.showAnswersTiming) return true; // Default behavior
    
    switch (quiz.showAnswersTiming) {
      case 'immediate':
        return true;
      case 'end':
        return attemptCompleted;
      case 'never':
        return false;
      case 'after_deadline':
        if (!quiz.dueDate) return attemptCompleted;
        return new Date() > new Date(quiz.dueDate);
      default:
        return attemptCompleted;
    }
  }
  
  // Filter quizzes by category, difficulty, or tags
  getFilteredQuizzes(): Quiz[] {
    let filtered = this.quizzes;
    
    // Filter by view mode
    const now = new Date();
    switch (this.viewMode) {
      case 'published':
        filtered = filtered.filter(q => q.published && (!q.publishAt || new Date(q.publishAt) <= now));
        break;
      case 'scheduled':
        filtered = filtered.filter(q => q.publishAt && new Date(q.publishAt) > now);
        break;
    }
    
    // Apply other filters
    if (this.filterCategory) {
      filtered = filtered.filter(q => q.category === this.filterCategory);
    }
    
    if (this.filterDifficulty) {
      filtered = filtered.filter(q => q.difficulty === this.filterDifficulty);
    }
    
    if (this.filterTag) {
      filtered = filtered.filter(q => 
        q.tags && q.tags.toLowerCase().includes(this.filterTag.toLowerCase())
      );
    }
    
    return filtered;
  }
  
  setViewMode(mode: 'published' | 'scheduled') {
    this.viewMode = mode;
  }
  
  getQuizCountByMode(mode: 'published' | 'scheduled'): number {
    const now = new Date();
    switch (mode) {
      case 'published':
        return this.quizzes.filter(q => q.published && (!q.publishAt || new Date(q.publishAt) <= now)).length;
      case 'scheduled':
        return this.quizzes.filter(q => q.publishAt && new Date(q.publishAt) > now).length;
    }
  }
  
  isScheduled(quiz: Quiz): boolean {
    if (!quiz.publishAt) return false;
    return new Date(quiz.publishAt) > new Date();
  }
  
  clearFilters() {
    this.filterCategory = '';
    this.filterDifficulty = '';
    this.filterTag = '';
  }
  
  hasActiveFilters(): boolean {
    return !!(this.filterCategory || this.filterDifficulty || this.filterTag);
  }
  
  // ========== GAMIFICATION FEATURES ==========
  
  // Timer zone calculation
  getTimerZone(): 'green' | 'yellow' | 'red' {
    if (!this.selectedQuiz) return 'green';
    const totalTime = (this.selectedQuiz.durationMin || 30) * 60;
    const percentage = (this.timeRemaining / totalTime) * 100;
    
    if (percentage > 50) return 'green';
    if (percentage > 25) return 'yellow';
    return 'red';
  }
  
  // Toggle flag on question
  toggleFlag(questionId: number) {
    if (this.flaggedQuestions.has(questionId)) {
      this.flaggedQuestions.delete(questionId);
    } else {
      this.flaggedQuestions.add(questionId);
    }
  }
  
  isFlagged(questionId: number): boolean {
    return this.flaggedQuestions.has(questionId);
  }
  
  // Get question status for mini-map
  getQuestionStatus(index: number): 'current' | 'answered' | 'flagged' | 'unseen' {
    const question = this.questions[index];
    if (!question.id) return 'unseen';
    
    if (index === this.currentQuestionIndex) return 'current';
    if (this.flaggedQuestions.has(question.id)) return 'flagged';
    if (this.currentAnswers[question.id]) return 'answered';
    return 'unseen';
  }
  
  // Track time spent on each question
  startQuestionTimer() {
    this.questionStartTime = new Date();
  }
  
  endQuestionTimer() {
    if (this.questionStartTime && this.questions[this.currentQuestionIndex]?.id) {
      const timeSpent = Math.floor((new Date().getTime() - this.questionStartTime.getTime()) / 1000);
      const questionId = this.questions[this.currentQuestionIndex].id!;
      this.questionTimings[questionId] = timeSpent;
      
      // Time banking: if answered quickly, bank the saved time
      const expectedTime = Math.floor(((this.selectedQuiz?.durationMin || 30) * 60) / this.questions.length);
      if (timeSpent < expectedTime * 0.5) {
        const savedTime = Math.floor(expectedTime * 0.5 - timeSpent);
        this.timeBanked += savedTime;
      }
    }
  }
  
  // Calculate achievements from attempt history
  calculateAchievements(): Array<{icon: string, title: string, description: string, color: string}> {
    const achievements: Array<{icon: string, title: string, description: string, color: string}> = [];
    
    if (this.myAttempts.length === 0) return achievements;
    
    // Speed Demon: Completed in <50% of allotted time
    const speedAttempts = this.myAttempts.filter((a: any) => {
      const quiz = this.quizzes.find(q => q.id === a.quizId);
      if (!quiz) return false;
      const expectedTime = (quiz.durationMin || 30) * 60;
      const actualTime = a.timeSpent || expectedTime;
      return actualTime < expectedTime * 0.5;
    });
    if (speedAttempts.length > 0) {
      achievements.push({
        icon: '⚡',
        title: 'Speed Demon',
        description: `Completed ${speedAttempts.length} quiz(es) in <50% time`,
        color: 'yellow'
      });
    }
    
    // Sharpshooter: 100% accuracy
    const perfectAttempts = this.myAttempts.filter((a: any) => a.score === 100);
    if (perfectAttempts.length > 0) {
      achievements.push({
        icon: '🎯',
        title: 'Sharpshooter',
        description: `${perfectAttempts.length} perfect score(s)`,
        color: 'blue'
      });
    }
    
    // Marathoner: 5 quizzes this week
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
    const recentAttempts = this.myAttempts.filter((a: any) => 
      new Date(a.submittedAt) > oneWeekAgo
    );
    if (recentAttempts.length >= 5) {
      achievements.push({
        icon: '🔥',
        title: 'Marathoner',
        description: `${recentAttempts.length} quizzes this week`,
        color: 'orange'
      });
    }
    
    // Comeback Kid: Improved by >20%
    const quizAttempts = new Map<number, any[]>();
    this.myAttempts.forEach((a: any) => {
      if (!quizAttempts.has(a.quizId)) {
        quizAttempts.set(a.quizId, []);
      }
      quizAttempts.get(a.quizId)!.push(a);
    });
    
    let comebacks = 0;
    quizAttempts.forEach((attempts) => {
      if (attempts.length >= 2) {
        attempts.sort((a, b) => new Date(a.submittedAt).getTime() - new Date(b.submittedAt).getTime());
        for (let i = 1; i < attempts.length; i++) {
          const improvement = attempts[i].score - attempts[i-1].score;
          if (improvement > 20) comebacks++;
        }
      }
    });
    
    if (comebacks > 0) {
      achievements.push({
        icon: '🧠',
        title: 'Comeback Kid',
        description: `Improved by >20% on ${comebacks} retake(s)`,
        color: 'purple'
      });
    }
    
    return achievements;
  }
  
  // Show streak animation
  showStreakEffect(message: string) {
    this.streakMessage = message;
    this.showStreakAnimation = true;
    setTimeout(() => {
      this.showStreakAnimation = false;
    }, 2000);
  }
}

