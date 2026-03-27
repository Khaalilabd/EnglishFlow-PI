import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-progress',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">My Progress</h1>

      <!-- Overall Progress -->
      <div class="bg-gradient-to-r from-[#2D5757] to-[#3D3D60] rounded-xl p-6 text-white">
        <h2 class="text-xl font-bold mb-4">Overall Progress</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <p class="text-sm opacity-90 mb-1">Courses Completed</p>
            <p class="text-4xl font-bold">1/3</p>
          </div>
          <div>
            <p class="text-sm opacity-90 mb-1">Total Hours</p>
            <p class="text-4xl font-bold">24.5</p>
          </div>
          <div>
            <p class="text-sm opacity-90 mb-1">Average Score</p>
            <p class="text-4xl font-bold">85%</p>
          </div>
        </div>
      </div>

      <!-- Course Progress -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Course Progress</h2>
        <div class="space-y-6">
          <div *ngFor="let course of courseProgress">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <span class="text-2xl">{{ course.icon }}</span>
                <div>
                  <h3 class="font-semibold text-gray-900">{{ course.title }}</h3>
                  <p class="text-sm text-gray-600">{{ course.completed }}/{{ course.total }} lessons completed</p>
                </div>
              </div>
              <span class="text-2xl font-bold" [ngClass]="{
                'text-green-600': course.progress >= 80,
                'text-yellow-600': course.progress >= 50 && course.progress < 80,
                'text-amber-600': course.progress < 50
              }">{{ course.progress }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-3">
              <div class="h-3 rounded-full transition-all duration-500"
                   [ngClass]="{
                     'bg-green-500': course.progress >= 80,
                     'bg-yellow-500': course.progress >= 50 && course.progress < 80,
                     'bg-amber-500': course.progress < 50
                   }"
                   [style.width.%]="course.progress"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Achievements -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Achievements</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div *ngFor="let achievement of achievements" 
               class="text-center p-4 rounded-lg"
               [ngClass]="achievement.unlocked ? 'bg-yellow-50' : 'bg-gray-100'">
            <div class="text-4xl mb-2" [ngClass]="!achievement.unlocked ? 'opacity-30' : ''">
              {{ achievement.icon }}
            </div>
            <p class="font-semibold text-sm" [ngClass]="achievement.unlocked ? 'text-gray-900' : 'text-gray-400'">
              {{ achievement.name }}
            </p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ProgressComponent {
  courseProgress = [
    { title: 'Grammar Basics', icon: '📚', completed: 6, total: 12, progress: 50 },
    { title: 'Conversation Practice', icon: '💬', completed: 3, total: 10, progress: 30 },
    { title: 'Business English', icon: '💼', completed: 12, total: 15, progress: 80 }
  ];

  achievements = [
    { name: 'First Lesson', icon: '🎯', unlocked: true },
    { name: 'Week Streak', icon: '🔥', unlocked: true },
    { name: 'Quiz Master', icon: '🏆', unlocked: false },
    { name: 'Course Complete', icon: '⭐', unlocked: false }
  ];
}
