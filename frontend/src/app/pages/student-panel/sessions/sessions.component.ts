import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SessionService, UserSession } from '../../../services/session.service';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sessions.component.html',
  styleUrls: ['./sessions.component.scss']
})
export class SessionsComponent implements OnInit {
  sessions: UserSession[] = [];
  loading = true;
  error = '';

  constructor(private sessionService: SessionService) {}

  ngOnInit(): void {
    this.loadSessions();
  }

  loadSessions(): void {
    this.loading = true;
    this.sessionService.getMySessions().subscribe({
      next: (sessions) => {
        this.sessions = sessions;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading sessions:', error);
        this.error = 'Failed to load sessions';
        this.loading = false;
      }
    });
  }

  terminateSession(sessionId: number): void {
    if (confirm('Are you sure you want to terminate this session?')) {
      this.sessionService.terminateSession(sessionId).subscribe({
        next: () => {
          this.loadSessions();
        },
        error: (error) => {
          console.error('Error terminating session:', error);
          alert('Failed to terminate session');
        }
      });
    }
  }

  terminateAllOthers(): void {
    if (confirm('Are you sure you want to terminate all other sessions?')) {
      this.sessionService.terminateAllOtherSessions().subscribe({
        next: () => {
          this.loadSessions();
        },
        error: (error) => {
          console.error('Error terminating sessions:', error);
          alert('Failed to terminate sessions');
        }
      });
    }
  }

  getDeviceIcon(deviceInfo: string): string {
    const lower = deviceInfo?.toLowerCase() || '';
    if (lower.includes('mobile') || lower.includes('android') || lower.includes('iphone')) {
      return 'smartphone';
    } else if (lower.includes('tablet') || lower.includes('ipad')) {
      return 'tablet';
    }
    return 'computer';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'EXPIRED':
        return 'bg-gray-100 text-gray-800';
      case 'TERMINATED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }
}
