import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { QuizService } from '../../../core/services/quiz.service';
import { Quiz } from '../../../core/models/quiz.model';

@Component({
  selector: 'app-quiz-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quiz-management.component.html'
})
export class QuizManagementComponent implements OnInit {
  quizzes: Quiz[] = [];
  filteredQuizzes: Quiz[] = [];
  loading = false;
  
  filterStatus: 'all' | 'published' | 'draft' = 'all';
  searchTerm = '';

  constructor(private quizService: QuizService) {}

  ngOnInit() {
    this.loadQuizzes();
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
                           (this.filterStatus === 'published' && quiz.published) ||
                           (this.filterStatus === 'draft' && !quiz.published);
      
      const matchesSearch = !this.searchTerm || 
                           quiz.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           quiz.description?.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      return matchesStatus && matchesSearch;
    });
  }

  onFilterChange(status: 'all' | 'published' | 'draft') {
    this.filterStatus = status;
    this.applyFilters();
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
}
