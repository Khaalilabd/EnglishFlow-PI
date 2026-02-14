import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quizzes',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">Quizzes</h1>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let quiz of quizzes" class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center justify-between mb-4">
            <span class="text-3xl">{{ quiz.icon }}</span>
            <span class="px-3 py-1 rounded-full text-xs font-semibold"
                  [ngClass]="{
                    'bg-green-100 text-green-700': quiz.status === 'completed',
                    'bg-yellow-100 text-yellow-700': quiz.status === 'available',
                    'bg-gray-100 text-gray-700': quiz.status === 'locked'
                  }">
              {{ quiz.status }}
            </span>
          </div>
          
          <h3 class="text-lg font-bold text-gray-900 mb-2">{{ quiz.title }}</h3>
          <p class="text-sm text-gray-600 mb-4">{{ quiz.course }}</p>
          
          <div class="flex items-center justify-between text-sm text-gray-600 mb-4">
            <span>⏱️ {{ quiz.duration }} min</span>
            <span>❓ {{ quiz.questions }} questions</span>
          </div>
          
          <button 
            [disabled]="quiz.status === 'locked'"
            class="w-full py-2 rounded-lg font-semibold text-sm transition-colors"
            [ngClass]="{
              'bg-[#2D5757] text-white hover:bg-[#3D3D60]': quiz.status === 'available',
              'bg-green-500 text-white': quiz.status === 'completed',
              'bg-gray-300 text-gray-500 cursor-not-allowed': quiz.status === 'locked'
            }">
            {{ quiz.status === 'completed' ? 'View Results' : quiz.status === 'available' ? 'Start Quiz' : 'Locked' }}
          </button>
          
          <p *ngIf="quiz.score" class="text-center mt-3 text-sm font-semibold text-green-600">
            Score: {{ quiz.score }}%
          </p>
        </div>
      </div>
    </div>
  `
})
export class QuizzesComponent {
  quizzes = [
    { title: 'Grammar Basics Quiz 1', course: 'Grammar Basics', icon: '📝', duration: 15, questions: 10, status: 'completed', score: 85 },
    { title: 'Conversation Practice Quiz', course: 'Conversation Practice', icon: '💬', duration: 20, questions: 15, status: 'available', score: null },
    { title: 'Business English Final', course: 'Business English', icon: '💼', duration: 30, questions: 20, status: 'locked', score: null }
  ];
}
