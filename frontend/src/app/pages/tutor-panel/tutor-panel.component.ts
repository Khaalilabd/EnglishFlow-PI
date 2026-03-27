import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-tutor-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tutor-panel.component.html',
  styleUrls: ['./tutor-panel.component.scss']
})
export class TutorPanelComponent {
  stats = [
    { icon: 'fas fa-users', label: 'Total Students', value: '124', change: '+12%', color: 'purple' },
    { icon: 'fas fa-book', label: 'Active Courses', value: '8', change: '+2', color: 'blue' },
    { icon: 'fas fa-clipboard-list', label: 'Quizzes Created', value: '45', change: '+5', color: 'orange' },
    { icon: 'fas fa-chart-line', label: 'Avg. Score', value: '78%', change: '+3%', color: 'green' }
  ];

  recentQuizzes = [
    { title: 'English Grammar Basics', students: 45, avgScore: 82, status: 'active', dueDate: '2026-02-20' },
    { title: 'Vocabulary Test - Unit 5', students: 38, avgScore: 75, status: 'active', dueDate: '2026-02-18' },
    { title: 'Reading Comprehension', students: 52, avgScore: 88, status: 'completed', dueDate: '2026-02-10' },
  ];

  upcomingClasses = [
    { course: 'Advanced English', time: '09:00 AM', students: 25, room: 'Room A1' },
    { course: 'Business English', time: '02:00 PM', students: 18, room: 'Room B2' },
    { course: 'IELTS Preparation', time: '04:30 PM', students: 30, room: 'Room C3' },
  ];

  recentActivity = [
    { student: 'Ahmed Ben Ali', action: 'completed quiz', quiz: 'Grammar Test', score: 85, time: '2 hours ago' },
    { student: 'Sara Mansouri', action: 'submitted assignment', quiz: 'Essay Writing', score: 92, time: '3 hours ago' },
    { student: 'Mohamed Trabelsi', action: 'completed quiz', quiz: 'Vocabulary', score: 78, time: '5 hours ago' },
  ];
}
