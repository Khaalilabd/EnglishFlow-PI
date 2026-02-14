import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-grades',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">My Grades</h1>

      <!-- Overall Stats -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Overall Average</p>
          <p class="text-4xl font-bold mt-2">85%</p>
          <p class="text-sm mt-2">↑ 3% from last month</p>
        </div>
        <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Completed</p>
          <p class="text-4xl font-bold mt-2">12</p>
          <p class="text-sm mt-2">Assignments</p>
        </div>
        <div class="bg-gradient-to-br from-orange-500 to-orange-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Pending</p>
          <p class="text-4xl font-bold mt-2">2</p>
          <p class="text-sm mt-2">Assignments</p>
        </div>
        <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Best Score</p>
          <p class="text-4xl font-bold mt-2">95%</p>
          <p class="text-sm mt-2">Business English</p>
        </div>
      </div>

      <!-- Grades by Course -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Grades by Course</h2>
        <div class="space-y-4">
          <div *ngFor="let grade of grades" class="border-b border-gray-200 pb-4 last:border-0">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <span class="text-2xl">{{ grade.icon }}</span>
                <div>
                  <h3 class="font-semibold text-gray-900">{{ grade.course }}</h3>
                  <p class="text-sm text-gray-600">{{ grade.assignments }} assignments</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-2xl font-bold" [ngClass]="{
                  'text-green-600': grade.average >= 80,
                  'text-yellow-600': grade.average >= 60 && grade.average < 80,
                  'text-red-600': grade.average < 60
                }">{{ grade.average }}%</p>
                <p class="text-sm text-gray-600">Average</p>
              </div>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="h-2 rounded-full transition-all duration-500"
                   [ngClass]="{
                     'bg-green-500': grade.average >= 80,
                     'bg-yellow-500': grade.average >= 60 && grade.average < 80,
                     'bg-red-500': grade.average < 60
                   }"
                   [style.width.%]="grade.average"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class GradesComponent {
  grades = [
    { course: 'Grammar Basics', icon: '📚', average: 82, assignments: 5 },
    { course: 'Conversation Practice', icon: '💬', average: 78, assignments: 4 },
    { course: 'Business English', icon: '💼', average: 95, assignments: 3 }
  ];
}
