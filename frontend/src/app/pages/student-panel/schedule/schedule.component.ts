import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">My Schedule</h1>
      
      <div class="bg-white rounded-xl shadow-sm p-6">
        <div class="grid grid-cols-7 gap-4 mb-4">
          <div *ngFor="let day of days" class="text-center font-semibold text-gray-700">
            {{ day }}
          </div>
        </div>
        
        <div class="grid grid-cols-7 gap-4">
          <div *ngFor="let date of dates" 
               class="aspect-square flex items-center justify-center rounded-lg hover:bg-gray-50 cursor-pointer"
               [ngClass]="{'bg-[#F59E0B] text-white font-bold': date === 15}">
            {{ date }}
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-bold text-gray-900 mb-4">Upcoming Classes</h2>
        <div class="space-y-3">
          <div *ngFor="let class of upcomingClasses" 
               class="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:border-[#F59E0B] transition-colors">
            <div class="text-3xl">{{ class.icon }}</div>
            <div class="flex-1">
              <h3 class="font-semibold text-gray-900">{{ class.title }}</h3>
              <p class="text-sm text-gray-600">{{ class.instructor }}</p>
            </div>
            <div class="text-right">
              <p class="font-semibold text-gray-900">{{ class.time }}</p>
              <p class="text-sm text-gray-600">{{ class.date }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ScheduleComponent {
  days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  dates = Array.from({length: 35}, (_, i) => i + 1);
  
  upcomingClasses = [
    { icon: '📚', title: 'Grammar Basics', instructor: 'Sarah Johnson', time: '10:00 AM', date: 'Today' },
    { icon: '💬', title: 'Conversation Practice', instructor: 'Michael Brown', time: '2:00 PM', date: 'Today' },
    { icon: '💼', title: 'Business English', instructor: 'Emily Davis', time: '4:00 PM', date: 'Tomorrow' }
  ];
}
