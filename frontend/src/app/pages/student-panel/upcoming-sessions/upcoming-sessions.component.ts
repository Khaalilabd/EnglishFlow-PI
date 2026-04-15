import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { Subject, interval, takeUntil } from 'rxjs';

interface UpcomingSession {
  id: number;
  onlineLessonId: number;
  sessionDate: string;
  sessionTime: string;
  status: string;
  courseName: string;
  lessonTitle: string;
  durationMinutes: number;
  timezone: string;
}

@Component({
  selector: 'app-upcoming-sessions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upcoming-sessions.component.html',
  styleUrl: './upcoming-sessions.component.scss'
})
export class UpcomingSessionsComponent implements OnInit, OnDestroy {
  sessions: UpcomingSession[] = [];
  loading = true;
  currentUserId: number | null = null;
  countdowns: { [key: number]: string } = {};
  
  private destroy$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUserId();
    if (this.currentUserId) {
      this.loadSessions();
      // Update countdowns every minute
      interval(60000)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => this.updateCountdowns());
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadSessions(): void {
    if (!this.currentUserId) return;

    this.http.get<UpcomingSession[]>(
      `${environment.apiUrl}/online-lessons/student/${this.currentUserId}/upcoming`
    ).subscribe({
      next: (data) => {
        this.sessions = data;
        this.updateCountdowns();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading sessions:', error);
        this.loading = false;
      }
    });
  }

  updateCountdowns(): void {
    const now = new Date();
    this.sessions.forEach(session => {
      const sessionDateTime = new Date(`${session.sessionDate}T${session.sessionTime}`);
      const diff = sessionDateTime.getTime() - now.getTime();

      if (diff > 0) {
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

        if (days > 0) {
          this.countdowns[session.id] = `${days}d ${hours}h`;
        } else if (hours > 0) {
          this.countdowns[session.id] = `${hours}h ${minutes}m`;
        } else {
          this.countdowns[session.id] = `${minutes}m`;
        }
      } else {
        this.countdowns[session.id] = 'Started';
      }
    });
  }

  canJoin(session: UpcomingSession): boolean {
    const sessionDateTime = new Date(`${session.sessionDate}T${session.sessionTime}`);
    const now = new Date();
    const minutesBefore = 15;
    const startTime = new Date(sessionDateTime.getTime() - minutesBefore * 60 * 1000);
    
    return now >= startTime && now <= new Date(sessionDateTime.getTime() + session.durationMinutes * 60 * 1000);
  }

  joinSession(session: UpcomingSession): void {
    // TODO: Implement video conference integration
    alert(`Joining session: ${session.lessonTitle}\nThis will open the video conference.`);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'scheduled':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300';
      case 'in-progress':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300';
      case 'completed':
        return 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
    }
  }
}
