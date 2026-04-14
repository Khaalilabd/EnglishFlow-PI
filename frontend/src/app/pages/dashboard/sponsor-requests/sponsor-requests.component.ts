import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../../core/services/notification.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-sponsor-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sponsor-requests.component.html'
})
export class SponsorRequestsComponent implements OnInit {
  requests: any[] = [];
  filteredRequests: any[] = [];
  loading = false;
  searchQuery = '';
  sortBy: 'name_asc' | 'name_desc' | 'amount_asc' | 'amount_desc' | 'level' = 'name_asc';
  private apiUrl = `${environment.apiUrl}/sponsors`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  ngOnInit() { this.loadRequests(); }

  loadRequests() {
    this.loading = true;
    this.http.get<any[]>(`${this.apiUrl}/pending`).subscribe({
      next: (data) => { this.requests = data; this.applyFilters(); this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  applyFilters() {
    const q = this.searchQuery.trim().toLowerCase();
    let result = q
      ? this.requests.filter(r =>
          r.name?.toLowerCase().includes(q) ||
          r.contactEmail?.toLowerCase().includes(q) ||
          r.clubName?.toLowerCase().includes(q)
        )
      : [...this.requests];

    const levelOrder: Record<string, number> = { GOLD: 1, SILVER: 2, BRONZE: 3 };
    result.sort((a, b) => {
      switch (this.sortBy) {
        case 'name_asc':    return (a.name || '').localeCompare(b.name || '');
        case 'name_desc':   return (b.name || '').localeCompare(a.name || '');
        case 'amount_asc':  return (a.contributionAmount || 0) - (b.contributionAmount || 0);
        case 'amount_desc': return (b.contributionAmount || 0) - (a.contributionAmount || 0);
        case 'level':       return (levelOrder[a.level] || 99) - (levelOrder[b.level] || 99);
        default:            return 0;
      }
    });
    this.filteredRequests = result;
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
