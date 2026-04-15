import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

interface OnlineLessonSchedule {
  lessonId: number;
  lessonTitle: string;
  courseTitle: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  canStart: boolean;
}

@Component({
  selector: 'app-tutor-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tutor-panel.component.html',
  styleUrls: ['./tutor-panel.component.scss']
})
export class TutorPanelComponent implements OnInit {
  tutorId: number = 0;
  onlineLessons: OnlineLessonSchedule[] = [];
  loadingLessons = false;

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {}
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

  ngOnInit(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser?.id) {
      this.tutorId = currentUser.id;
      this.loadOnlineLessons();
    }
  }

  loadOnlineLessons(): void {
    this.loadingLessons = true;
    this.http.get<any[]>(`${environment.apiUrl}/online-lessons/tutor/${this.tutorId}/scheduled`)
      .subscribe({
        next: (lessons) => {
          this.onlineLessons = lessons.map(lesson => ({
            ...lesson,
            canStart: this.canStartLesson(lesson)
          }));
          this.loadingLessons = false;
        },
        error: (err) => {
          console.error('Failed to load online lessons:', err);
          this.loadingLessons = false;
        }
      });
  }

  canStartLesson(lesson: any): boolean {
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[lesson.dayOfWeek];
    if (now.getDay() !== lessonDay) return false;

    const [startH, startM] = lesson.startTime.split(':').map(Number);
    const [endH, endM] = lesson.endTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    // Enable button 15 minutes before start time until end time
    return nowMinutes >= startMinutes - 15 && nowMinutes <= endMinutes;
  }

  getDayName(dayOfWeek: string): string {
    const days: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return days[dayOfWeek] || dayOfWeek;
  }

  startMeeting(lesson: OnlineLessonSchedule): void {
    const roomId = `lesson-${lesson.lessonId}`;
    this.router.navigate(['/meeting', roomId], {
      queryParams: { lessonId: lesson.lessonId }
    });
  }
}
