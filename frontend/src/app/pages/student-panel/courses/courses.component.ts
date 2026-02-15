import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Course {
  id: number;
  title: string;
  instructor: string;
  instructorAvatar: string;
  progress: number;
  icon: string;
  color: string;
  lessons: number;
  duration: string;
  enrolled: string;
  rating: number;
  students: number;
}

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="space-y-6">
      <!-- Page Header -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">My Courses</h1>
          <p class="text-gray-600 mt-1">Continue learning and track your progress</p>
        </div>
        <button class="px-4 py-2 bg-[#F59E0B] text-white rounded-lg hover:bg-[#e5ac4f] transition-colors">
          Browse More Courses
        </button>
      </div>

      <!-- Courses Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let course of courses" class="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden">
          <!-- Course Header -->
          <div class="p-6" [ngClass]="course.color">
            <div class="flex items-center justify-between mb-4">
              <span class="text-5xl">{{ course.icon }}</span>
              <span class="px-3 py-1 bg-white rounded-full text-sm font-semibold text-gray-700">
                {{ course.progress }}%
              </span>
            </div>
            <h3 class="text-xl font-bold text-gray-900">{{ course.title }}</h3>
          </div>

          <!-- Course Body -->
          <div class="p-6">
            <!-- Instructor -->
            <div class="flex items-center gap-3 mb-4">
              <img [src]="course.instructorAvatar" alt="{{ course.instructor }}" class="w-10 h-10 rounded-full">
              <div>
                <p class="text-sm font-semibold text-gray-900">{{ course.instructor }}</p>
                <div class="flex items-center gap-1">
                  <span class="text-yellow-400">★</span>
                  <span class="text-sm text-gray-600">{{ course.rating }}</span>
                </div>
              </div>
            </div>

            <!-- Progress Bar -->
            <div class="mb-4">
              <div class="flex justify-between text-sm text-gray-600 mb-2">
                <span>Progress</span>
                <span>{{ course.progress }}% Complete</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div 
                  class="bg-gradient-to-r from-[#F59E0B] to-[#C84630] h-2 rounded-full transition-all duration-500"
                  [style.width.%]="course.progress"
                ></div>
              </div>
            </div>

            <!-- Course Info -->
            <div class="flex items-center justify-between text-sm text-gray-600 mb-4">
              <span>📚 {{ course.lessons }} Lessons</span>
              <span>⏱️ {{ course.duration }}</span>
            </div>

            <!-- Action Button -->
            <button class="w-full py-2.5 bg-[#2D5757] text-white rounded-lg hover:bg-[#3D3D60] transition-colors font-semibold">
              Continue Learning
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CoursesComponent {
  courses: Course[] = [
    {
      id: 1,
      title: 'Grammar Basics',
      instructor: 'Sarah Johnson',
      instructorAvatar: 'https://i.pravatar.cc/100?img=1',
      progress: 50,
      icon: '📚',
      color: 'bg-amber-100',
      lessons: 12,
      duration: '8 weeks',
      enrolled: '2 months ago',
      rating: 4.8,
      students: 1250
    },
    {
      id: 2,
      title: 'Conversation Practice',
      instructor: 'Michael Brown',
      instructorAvatar: 'https://i.pravatar.cc/100?img=2',
      progress: 30,
      icon: '💬',
      color: 'bg-yellow-100',
      lessons: 10,
      duration: '6 weeks',
      enrolled: '1 month ago',
      rating: 4.9,
      students: 980
    },
    {
      id: 3,
      title: 'Business English',
      instructor: 'Emily Davis',
      instructorAvatar: 'https://i.pravatar.cc/100?img=3',
      progress: 80,
      icon: '💼',
      color: 'bg-green-100',
      lessons: 15,
      duration: '10 weeks',
      enrolled: '3 months ago',
      rating: 4.7,
      students: 1500
    }
  ];
}
