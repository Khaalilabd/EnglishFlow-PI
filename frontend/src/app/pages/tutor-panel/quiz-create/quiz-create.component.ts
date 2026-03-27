import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { QuizService } from '../../../core/services/quiz.service';
import { Quiz, Question, QuestionType } from '../../../core/models/quiz.model';

@Component({
  selector: 'app-quiz-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-create.component.html'
})
export class QuizCreateComponent implements OnInit {
  isEditMode = false;
  quizId?: number;
  
  quiz: Quiz = {
    title: '',
    description: '',
    maxScore: 100,
    passingScore: 60,
    published: false,
    durationMin: 30,
    shuffleQuestions: false,
    shuffleOptions: false,
    showAnswersTiming: 'end',
    difficulty: 'medium',
    category: '',
    questions: []
  };

  currentQuestion: Question = this.getEmptyQuestion();
  editingQuestionIndex: number | null = null;

  questionTypes: { value: QuestionType; label: string }[] = [
    { value: 'MCQ', label: 'Multiple Choice' },
    { value: 'TRUE_FALSE', label: 'True/False' },
    { value: 'OPEN', label: 'Open Question' }
  ];

  difficulties = ['easy', 'medium', 'hard'];
  showAnswersOptions = [
    { value: 'immediate', label: 'Immediately after each question' },
    { value: 'end', label: 'At the end of quiz' },
    { value: 'never', label: 'Never show answers' },
    { value: 'after_deadline', label: 'After deadline' }
  ];

  constructor(
    private quizService: QuizService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.quizId = +params['id'];
        this.loadQuiz();
      }
    });
  }

  loadQuiz() {
    if (this.quizId) {
      this.quizService.getQuizById(this.quizId).subscribe({
        next: (quiz) => {
          this.quiz = quiz;
          if (!this.quiz.questions) {
            this.quiz.questions = [];
          }
        },
        error: (error) => {
          console.error('Error loading quiz:', error);
          alert('Failed to load quiz');
        }
      });
    }
  }

  getEmptyQuestion(): Question {
    return {
      content: '',
      type: 'MCQ',
      options: '',
      correctAnswer: '',
      points: 10,
      orderIndex: this.quiz.questions?.length || 0
    };
  }

  addQuestion() {
    if (!this.currentQuestion.content || !this.currentQuestion.correctAnswer) {
      alert('Please fill in question content and correct answer');
      return;
    }

    if (this.editingQuestionIndex !== null) {
      this.quiz.questions![this.editingQuestionIndex] = { ...this.currentQuestion };
      this.editingQuestionIndex = null;
    } else {
      this.quiz.questions!.push({ ...this.currentQuestion });
    }

    this.currentQuestion = this.getEmptyQuestion();
    this.updateMaxScore();
  }

  editQuestion(index: number) {
    this.currentQuestion = { ...this.quiz.questions![index] };
    this.editingQuestionIndex = index;
  }

  deleteQuestion(index: number) {
    if (confirm('Are you sure you want to delete this question?')) {
      this.quiz.questions!.splice(index, 1);
      this.updateMaxScore();
    }
  }

  cancelEdit() {
    this.currentQuestion = this.getEmptyQuestion();
    this.editingQuestionIndex = null;
  }

  updateMaxScore() {
    this.quiz.maxScore = this.quiz.questions!.reduce((sum, q) => sum + q.points, 0);
  }

  saveQuiz() {
    if (!this.quiz.title || !this.quiz.description) {
      alert('Please fill in quiz title and description');
      return;
    }

    if (!this.quiz.questions || this.quiz.questions.length === 0) {
      alert('Please add at least one question');
      return;
    }

    const saveObservable = this.isEditMode
      ? this.quizService.updateQuiz(this.quizId!, this.quiz)
      : this.quizService.createQuiz(this.quiz);

    saveObservable.subscribe({
      next: () => {
        alert(this.isEditMode ? 'Quiz updated successfully!' : 'Quiz created successfully!');
        this.router.navigate(['/tutor-panel/quiz-management']);
      },
      error: (error) => {
        console.error('Error saving quiz:', error);
        alert('Failed to save quiz');
      }
    });
  }

  cancel() {
    this.router.navigate(['/tutor-panel/quiz-management']);
  }
}
