import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuizService } from '../../../core/services/quiz.service';
import { CourseService } from '../../../core/services/course.service';
import { Quiz } from '../../../core/models/quiz.model';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-quiz-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './quiz-management.component.html'
})
export class QuizManagementComponent implements OnInit {
  Math = Math;
  quizzes: Quiz[] = [];
  filteredQuizzes: Quiz[] = [];
  courses: Course[] = [];
  loading = false;
  
  filterStatus: 'all' | 'published' | 'draft' | 'scheduled' = 'all';
  searchTerm = '';
  showFilters = false;
  filterCategory = '';
  filterDifficulty = '';
  filterTag = '';

  // Modal state
  showCreateModal = false;
  wizardStep = 1;
  totalWizardSteps = 3;
  isCreating = false;
  draftQuestions: any[] = [];
  previewMode = false;
  newQuiz: Quiz = {
    title: '',
    description: '',
    category: 'grammar',
    difficulty: 'medium',
    durationMin: 30,
    maxScore: 100,
    passingScore: 60,
    published: false,
    shuffleQuestions: false,
    shuffleOptions: false,
    showAnswersTiming: 'end',
    tags: ''
  };

  newQuestion: any = {
    content: '',
    type: 'MCQ',
    options: '',
    correctAnswer: '',
    points: 10,
    partialCreditEnabled: false
  };

  constructor(
    private quizService: QuizService,
    private courseService: CourseService
  ) {}

  ngOnInit() {
    this.loadQuizzes();
    this.loadCourses();
  }

  loadCourses() {
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
      }
    });
  }

  loadQuizzes() {
    this.loading = true;
    this.quizService.getAllQuizzes().subscribe({
      next: (quizzes) => {
        this.quizzes = quizzes;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading quizzes:', error);
        this.loading = false;
      }
    });
  }

  applyFilters() {
    this.filteredQuizzes = this.quizzes.filter(quiz => {
      const matchesStatus = this.filterStatus === 'all' || 
                           (this.filterStatus === 'published' && quiz.published && !this.isScheduled(quiz)) ||
                           (this.filterStatus === 'draft' && !quiz.published && !quiz.publishAt) ||
                           (this.filterStatus === 'scheduled' && this.isScheduled(quiz));
      
      const matchesSearch = !this.searchTerm || 
                           quiz.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           quiz.description?.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesCategory = !this.filterCategory || quiz.category === this.filterCategory;
      const matchesDifficulty = !this.filterDifficulty || quiz.difficulty === this.filterDifficulty;
      const matchesTag = !this.filterTag || 
                        (quiz.tags && quiz.tags.toLowerCase().includes(this.filterTag.toLowerCase()));
      
      return matchesStatus && matchesSearch && matchesCategory && matchesDifficulty && matchesTag;
    });
  }

  onFilterChange(status: 'all' | 'published' | 'draft' | 'scheduled') {
    this.filterStatus = status;
    this.applyFilters();
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  clearFilters() {
    this.filterCategory = '';
    this.filterDifficulty = '';
    this.filterTag = '';
    this.applyFilters();
  }

  hasActiveFilters(): boolean {
    return !!(this.filterCategory || this.filterDifficulty || this.filterTag);
  }

  getQuizCountByStatus(status: 'all' | 'published' | 'draft' | 'scheduled'): number {
    if (status === 'all') return this.quizzes.length;
    if (status === 'published') return this.quizzes.filter(q => q.published && !this.isScheduled(q)).length;
    if (status === 'draft') return this.quizzes.filter(q => !q.published && !q.publishAt).length;
    if (status === 'scheduled') return this.quizzes.filter(q => this.isScheduled(q)).length;
    return 0;
  }

  isScheduled(quiz: Quiz): boolean {
    if (!quiz.publishAt) return false;
    return new Date(quiz.publishAt) > new Date();
  }

  formatScheduledDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  onSearch(event: Event) {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  deleteQuiz(id: number) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      this.quizService.deleteQuiz(id).subscribe({
        next: () => {
          this.loadQuizzes();
        },
        error: (error) => {
          console.error('Error deleting quiz:', error);
          alert('Failed to delete quiz');
        }
      });
    }
  }

  togglePublish(quiz: Quiz) {
    const updatedQuiz = { ...quiz, published: !quiz.published };
    this.quizService.updateQuiz(quiz.id!, updatedQuiz).subscribe({
      next: () => {
        this.loadQuizzes();
      },
      error: (error) => {
        console.error('Error updating quiz:', error);
        alert('Failed to update quiz');
      }
    });
  }

  getDifficultyColor(difficulty?: string): string {
    switch(difficulty) {
      case 'easy': return 'bg-green-100 text-green-600';
      case 'medium': return 'bg-yellow-100 text-yellow-600';
      case 'hard': return 'bg-red-100 text-red-600';
      default: return 'bg-gray-100 text-gray-600';
    }
  }

  openCreateModal() {
    this.showCreateModal = true;
    this.wizardStep = 1;
    this.draftQuestions = [];
    this.previewMode = false;
    this.newQuiz = {
      title: '',
      description: '',
      category: 'grammar',
      difficulty: 'medium',
      durationMin: 30,
      maxScore: 100,
      passingScore: 60,
      published: false,
      shuffleQuestions: false,
      shuffleOptions: false,
      showAnswersTiming: 'end',
      tags: ''
    };
  }

  editQuiz(quiz: Quiz) {
    // Populate the form with existing quiz data
    this.newQuiz = { ...quiz };
    
    // Load questions for this quiz
    if (quiz.id) {
      this.loading = true;
      this.quizService.getQuestionsByQuizId(quiz.id).subscribe({
        next: (questions) => {
          this.draftQuestions = questions;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading questions:', error);
          this.draftQuestions = [];
          this.loading = false;
        }
      });
    }
    
    // Open the modal in edit mode
    this.wizardStep = 1;
    this.previewMode = false;
    this.showCreateModal = true;
  }

  closeCreateModal() {
    if (this.wizardStep > 1 && !confirm('Are you sure? Your progress will be lost.')) {
      return;
    }
    this.showCreateModal = false;
    this.wizardStep = 1;
    this.draftQuestions = [];
    this.previewMode = false;
  }

  nextWizardStep() {
    if (this.wizardStep === 1 && !this.validateBasicInfo()) {
      return;
    }
    if (this.wizardStep < this.totalWizardSteps) {
      this.wizardStep++;
    }
  }

  previousWizardStep() {
    if (this.wizardStep > 1) {
      this.wizardStep--;
    }
  }

  validateBasicInfo(): boolean {
    if (!this.newQuiz.title?.trim()) {
      alert('Please enter a quiz title');
      return false;
    }
    if (!this.newQuiz.description?.trim()) {
      alert('Please enter a quiz description');
      return false;
    }
    return true;
  }

  setDuration(minutes: number) {
    this.newQuiz.durationMin = minutes;
  }

  addDraftQuestion() {
    if (!this.newQuestion.content?.trim()) {
      alert('Please enter question content');
      return;
    }
    const question = { ...this.newQuestion };
    question.orderIndex = this.draftQuestions.length;
    this.draftQuestions.push(question);
    this.resetQuestionForm();
  }

  removeDraftQuestion(index: number) {
    this.draftQuestions.splice(index, 1);
    // Update order indices
    this.draftQuestions.forEach((q, i) => q.orderIndex = i);
  }

  togglePreview() {
    this.previewMode = !this.previewMode;
  }

  finishWizard() {
    if (this.draftQuestions.length === 0) {
      alert('Please add at least one question');
      return;
    }

    this.isCreating = true;

    // Check if we're editing (quiz has an id) or creating new
    if (this.newQuiz.id) {
      // Update existing quiz
      this.quizService.updateQuiz(this.newQuiz.id, this.newQuiz).subscribe({
        next: (updatedQuiz) => {
          // Get existing question IDs
          this.quizService.getQuestionsByQuizId(this.newQuiz.id!).subscribe({
            next: (existingQuestions) => {
              const existingQuestionIds = new Set(existingQuestions.map(q => q.id));
              const draftQuestionIds = new Set(this.draftQuestions.filter(q => q.id).map(q => q.id));
              
              // Delete questions that were removed
              const questionsToDelete = existingQuestions.filter(q => !draftQuestionIds.has(q.id));
              let deleteCount = 0;
              
              const deletePromises = questionsToDelete.map(q => 
                new Promise((resolve) => {
                  this.quizService.deleteQuestion(q.id!).subscribe({
                    next: () => resolve(true),
                    error: () => resolve(false)
                  });
                })
              );
              
              Promise.all(deletePromises).then(() => {
                // Add or update questions
                let processedCount = 0;
                const totalQuestions = this.draftQuestions.length;
                
                this.draftQuestions.forEach((question, index) => {
                  question.quizId = this.newQuiz.id;
                  question.orderIndex = index;
                  
                  if (question.id && existingQuestionIds.has(question.id)) {
                    // Update existing question
                    this.quizService.updateQuestion(question.id, question).subscribe({
                      next: () => {
                        processedCount++;
                        if (processedCount === totalQuestions) {
                          this.isCreating = false;
                          this.loadQuizzes();
                          this.closeCreateModal();
                          alert('Quiz updated successfully!');
                        }
                      },
                      error: (error) => {
                        console.error('Error updating question:', error);
                        this.isCreating = false;
                      }
                    });
                  } else {
                    // Create new question
                    this.quizService.createQuestion(question).subscribe({
                      next: () => {
                        processedCount++;
                        if (processedCount === totalQuestions) {
                          this.isCreating = false;
                          this.loadQuizzes();
                          this.closeCreateModal();
                          alert('Quiz updated successfully!');
                        }
                      },
                      error: (error) => {
                        console.error('Error adding question:', error);
                        this.isCreating = false;
                      }
                    });
                  }
                });
              });
            },
            error: (error) => {
              console.error('Error loading existing questions:', error);
              this.isCreating = false;
            }
          });
        },
        error: (error) => {
          console.error('Error updating quiz:', error);
          alert('Error updating quiz');
          this.isCreating = false;
        }
      });
    } else {
      // Create new quiz
      this.quizService.createQuiz(this.newQuiz).subscribe({
        next: (createdQuiz) => {
          // Then add all questions
          let questionsAdded = 0;
          const totalQuestions = this.draftQuestions.length;

          this.draftQuestions.forEach(question => {
            question.quizId = createdQuiz.id;
            this.quizService.createQuestion(question).subscribe({
              next: () => {
                questionsAdded++;
                if (questionsAdded === totalQuestions) {
                  this.isCreating = false;
                  this.loadQuizzes();
                  this.closeCreateModal();
                  alert('Quiz created successfully!');
                }
              },
              error: (error) => {
                console.error('Error adding question:', error);
                this.isCreating = false;
              }
            });
          });
        },
        error: (error) => {
          console.error('Error creating quiz:', error);
          alert('Error creating quiz');
          this.isCreating = false;
        }
      });
    }
  }

  resetQuestionForm() {
    this.newQuestion = {
      content: '',
      type: 'MCQ',
      options: '',
      correctAnswer: '',
      points: 10,
      partialCreditEnabled: false
    };
  }
}
