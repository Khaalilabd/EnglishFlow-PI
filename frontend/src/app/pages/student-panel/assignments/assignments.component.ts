import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-assignments',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-3xl font-bold text-gray-900">Assignments</h1>
        <div class="flex gap-2">
          <button class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">All</button>
          <button class="px-4 py-2 bg-[#F59E0B] text-white rounded-lg">Pending</button>
          <button class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">Completed</button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div *ngFor="let assignment of assignments" 
             class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-shadow">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-lg font-bold text-gray-900">{{ assignment.title }}</h3>
              <p class="text-sm text-gray-600">{{ assignment.course }}</p>
            </div>
            <span class="px-3 py-1 rounded-full text-xs font-semibold"
                  [ngClass]="{
                    'bg-red-100 text-red-700': assignment.status === 'overdue',
                    'bg-yellow-100 text-yellow-700': assignment.status === 'pending',
                    'bg-green-100 text-green-700': assignment.status === 'completed'
                  }">
              {{ assignment.status }}
            </span>
          </div>

          <p class="text-gray-700 mb-4">{{ assignment.description }}</p>

          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600">Due: {{ assignment.dueDate }}</span>
            <button class="px-4 py-2 bg-[#2D5757] text-white rounded-lg hover:bg-[#3D3D60] transition-colors">
              {{ assignment.status === 'completed' ? 'View' : 'Submit' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AssignmentsComponent {
  assignments = [
    {
      title: 'Essay Writing',
      course: 'Grammar Basics',
      description: 'Write a 500-word essay about your favorite book',
      dueDate: 'Feb 20, 2026',
      status: 'pending'
    },
    {
      title: 'Conversation Recording',
      course: 'Conversation Practice',
      description: 'Record a 5-minute conversation with a partner',
      dueDate: 'Feb 18, 2026',
      status: 'overdue'
    },
    {
      title: 'Business Presentation',
      course: 'Business English',
      description: 'Prepare a presentation about your company',
      dueDate: 'Feb 15, 2026',
      status: 'completed'
    }
  ];
}
