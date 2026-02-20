import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminSessionService, SessionSearchRequest } from '../../../services/admin-session.service';

@Component({
  selector: 'app-admin-sessions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-sessions.component.html',
  styleUrls: ['./admin-sessions.component.scss']
})
export class AdminSessionsComponent implements OnInit {
  sessions: any[] = [];
  suspiciousSessions: any[] = [];
  statistics: any = null;
  loading = false;
  error = '';
  
  // Filters
  searchRequest: SessionSearchRequest = {
    page: 0,
    size: 20,
    sortBy: 'lastActivity',
    sortDirection: 'DESC'
  };
  
  filterOptions: any = null;
  showFilters = false;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  
  // Make Math available in template
  Math = Math;

  constructor(private adminSessionService: AdminSessionService) {}

  ngOnInit(): void {
    this.loadStatistics();
    this.loadSessions();
    this.loadSuspiciousSessions();
    this.loadFilterOptions();
  }

  loadStatistics(): void {
    this.adminSessionService.getSessionStatistics(30).subscribe({
      next: (stats) => {
        this.statistics = stats;
      },
      error: (error) => {
        console.error('Error loading statistics:', error);
      }
    });
  }

  loadSessions(): void {
    this.loading = true;
    this.error = '';
    
    console.log('🔍 Loading sessions with request:', this.searchRequest);
    
    this.adminSessionService.searchSessions(this.searchRequest).subscribe({
      next: (response) => {
        console.log('✅ Sessions loaded successfully:', response);
        this.sessions = response.content;
        this.currentPage = response.number;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Error loading sessions:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error details:', error.error);
        this.error = `Failed to load sessions: ${error.error?.message || error.message || 'Unknown error'}`;
        this.loading = false;
      }
    });
  }

  loadSuspiciousSessions(): void {
    this.adminSessionService.getSuspiciousSessions().subscribe({
      next: (sessions) => {
        this.suspiciousSessions = sessions;
      },
      error: (error) => {
        console.error('Error loading suspicious sessions:', error);
      }
    });
  }

  loadFilterOptions(): void {
    this.adminSessionService.getFilterOptions().subscribe({
      next: (options) => {
        this.filterOptions = options;
      },
      error: (error) => {
        console.error('Error loading filter options:', error);
      }
    });
  }

  applyFilters(): void {
    this.searchRequest.page = 0;
    this.loadSessions();
  }

  clearFilters(): void {
    this.searchRequest = {
      page: 0,
      size: 20,
      sortBy: 'lastActivity',
      sortDirection: 'DESC'
    };
    this.loadSessions();
  }

  applyQuickFilter(filter: string): void {
    this.searchRequest.quickFilter = filter;
    this.searchRequest.page = 0;
    this.loadSessions();
  }

  terminateSession(sessionId: number): void {
    if (confirm('Are you sure you want to terminate this session?')) {
      this.adminSessionService.terminateSession(sessionId, 'ADMIN_TERMINATED').subscribe({
        next: () => {
          this.loadSessions();
          this.loadSuspiciousSessions();
          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error terminating session:', error);
          alert('Failed to terminate session');
        }
      });
    }
  }

  terminateAllUserSessions(userId: number): void {
    if (confirm('Are you sure you want to terminate ALL sessions for this user?')) {
      this.adminSessionService.terminateAllUserSessions(userId, 'ADMIN_TERMINATED').subscribe({
        next: (response) => {
          alert(`Terminated ${response.terminatedCount} sessions`);
          this.loadSessions();
          this.loadSuspiciousSessions();
          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error terminating sessions:', error);
          alert('Failed to terminate sessions');
        }
      });
    }
  }

  forceCleanup(): void {
    if (confirm('Force cleanup of expired sessions?')) {
      this.adminSessionService.forceCleanup().subscribe({
        next: () => {
          alert('Cleanup completed successfully');
          this.loadSessions();
          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error during cleanup:', error);
          alert('Failed to cleanup sessions');
        }
      });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.searchRequest.page = this.currentPage + 1;
      this.loadSessions();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.searchRequest.page = this.currentPage - 1;
      this.loadSessions();
    }
  }

  goToPage(page: number): void {
    this.searchRequest.page = page;
    this.loadSessions();
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

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  }

  getDeviceIcon(deviceInfo: string): string {
    const lower = deviceInfo?.toLowerCase() || '';
    if (lower.includes('mobile') || lower.includes('android') || lower.includes('iphone')) {
      return 'fas fa-mobile-alt';
    } else if (lower.includes('tablet') || lower.includes('ipad')) {
      return 'fas fa-tablet-alt';
    }
    return 'fas fa-desktop';
  }
}
