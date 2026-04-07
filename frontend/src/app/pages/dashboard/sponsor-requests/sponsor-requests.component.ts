import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SponsorService } from '../../../core/services/sponsor.service';
import { Sponsor } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-sponsor-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsor-requests.component.html'
})
export class SponsorRequestsComponent implements OnInit {
  requests: any[] = [];
  loading = false;
  private apiUrl = `${environment.apiUrl}/sponsors`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  ngOnInit() { this.loadRequests(); }

  loadRequests() {
    this.loading = true;
    this.http.get<any[]>(`${this.apiUrl}/pending`).subscribe({
      next: (data) => { this.requests = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  approve(id: number) {
    this.http.post<any>(`${this.apiUrl}/${id}/approve`, {}).subscribe({
      next: () => {
        this.notificationService.success('Approved', 'Sponsor request approved successfully');
        this.loadRequests();
      },
      error: () => this.notificationService.error('Error', 'Failed to approve')
    });
  }

  reject(id: number) {
    this.http.post<any>(`${this.apiUrl}/${id}/reject`, {}).subscribe({
      next: () => {
        this.notificationService.success('Rejected', 'Sponsor request rejected');
        this.loadRequests();
      },
      error: () => this.notificationService.error('Error', 'Failed to reject')
    });
  }

  getLevelBadge(level: string): string {
    const map: Record<string, string> = { GOLD: '🥇 Gold', SILVER: '🥈 Silver', BRONZE: '🥉 Bronze' };
    return map[level] || level;
  }

  getLevelClass(level: string): string {
    const map: Record<string, string> = {
      GOLD: 'bg-yellow-100 text-yellow-800',
      SILVER: 'bg-gray-100 text-gray-700',
      BRONZE: 'bg-orange-100 text-orange-800'
    };
    return map[level] || 'bg-gray-100 text-gray-700';
  }
}
