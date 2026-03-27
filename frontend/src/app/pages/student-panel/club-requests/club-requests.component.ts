import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { Club } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './club-requests.component.html',
  styleUrls: ['./club-requests.component.scss']
})
export class ClubRequestsComponent implements OnInit {
  loading = false;
  error: string | null = null;
  requests: Club[] = [];

  constructor(
    private clubService: ClubService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.loading = true;
    this.error = null;
    
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id || currentUser.id === 0) {
      this.error = 'User not authenticated. Please log in again.';
      this.loading = false;
      console.error('Invalid user or user ID:', currentUser);
      return;
    }

    console.log('Loading clubs for user ID:', currentUser.id);
    this.clubService.getClubsByUser(currentUser.id).subscribe({
      next: (clubs) => {
        this.requests = clubs;
        this.loading = false;
        console.log('Loaded clubs:', clubs);
      },
      error: (err) => {
        console.error('Error loading club requests:', err);
        this.error = 'Failed to load club requests. Please try again.';
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string | undefined): string {
    switch (status) {
      case 'APPROVED':
        return 'status-approved';
      case 'REJECTED':
        return 'status-rejected';
      case 'PENDING':
      default:
        return 'status-pending';
    }
  }

  getStatusLabel(status: string | undefined): string {
    switch (status) {
      case 'APPROVED':
        return 'Approuvé';
      case 'REJECTED':
        return 'Rejeté';
      case 'PENDING':
      default:
        return 'En attente';
    }
  }
}
