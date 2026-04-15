import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface ScheduleDTO {
  dayOfWeek: number;
  time: string;
}

interface OnlineLessonConfig {
  lessonId: number;
  duration: number;
  timezone: string;
  startDate: string;
  endDate: string;
  schedules: ScheduleDTO[];
}

@Component({
  selector: 'app-online-lesson-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './online-lesson-config.component.html',
  styleUrl: './online-lesson-config.component.scss'
})
export class OnlineLessonConfigComponent implements OnInit {
  lessonId!: number;
  courseId!: number;
  
  config: OnlineLessonConfig = {
    lessonId: 0,
    duration: 90,
    timezone: 'UTC+1',
    startDate: new Date().toISOString().split('T')[0],
    endDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    schedules: []
  };

  newSchedule: ScheduleDTO = {
    dayOfWeek: 1,
    time: '22:00'
  };

  days = [
    { value: 0, label: 'Sunday' },
    { value: 1, label: 'Monday' },
    { value: 2, label: 'Tuesday' },
    { value: 3, label: 'Wednesday' },
    { value: 4, label: 'Thursday' },
    { value: 5, label: 'Friday' },
    { value: 6, label: 'Saturday' }
  ];

  timezones = [
    'UTC-12', 'UTC-11', 'UTC-10', 'UTC-9', 'UTC-8', 'UTC-7', 'UTC-6', 'UTC-5',
    'UTC-4', 'UTC-3', 'UTC-2', 'UTC-1', 'UTC', 'UTC+1', 'UTC+2', 'UTC+3',
    'UTC+4', 'UTC+5', 'UTC+6', 'UTC+7', 'UTC+8', 'UTC+9', 'UTC+10', 'UTC+11', 'UTC+12'
  ];

  loading = false;
  saving = false;
  message = '';
  messageType: 'success' | 'error' = 'success';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.lessonId = Number(this.route.snapshot.paramMap.get('lessonId'));
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.config.lessonId = this.lessonId;
  }

  addSchedule(): void {
    if (this.newSchedule.time) {
      this.config.schedules.push({ ...this.newSchedule });
      this.newSchedule = { dayOfWeek: 1, time: '22:00' };
    }
  }

  removeSchedule(index: number): void {
    this.config.schedules.splice(index, 1);
  }

  getDayLabel(dayOfWeek: number): string {
    return this.days.find(d => d.value === dayOfWeek)?.label || '';
  }

  saveConfiguration(): void {
    if (!this.config.duration || !this.config.startDate || !this.config.endDate) {
      this.showMessage('Please fill in all required fields', 'error');
      return;
    }

    if (this.config.schedules.length === 0) {
      this.showMessage('Please add at least one schedule', 'error');
      return;
    }

    this.saving = true;
    this.http.post(`${environment.apiUrl}/online-lessons/configure`, this.config)
      .subscribe({
        next: () => {
          this.showMessage('Online lesson configured successfully!', 'success');
          setTimeout(() => {
            this.router.navigate([`/tutor-panel/courses/${this.courseId}`]);
          }, 1500);
        },
        error: (error) => {
          console.error('Error saving configuration:', error);
          this.showMessage('Failed to save configuration. Please try again.', 'error');
          this.saving = false;
        }
      });
  }

  goBack(): void {
    this.router.navigate([`/tutor-panel/courses/${this.courseId}`]);
  }

  private showMessage(msg: string, type: 'success' | 'error'): void {
    this.message = msg;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 3000);
  }
}
