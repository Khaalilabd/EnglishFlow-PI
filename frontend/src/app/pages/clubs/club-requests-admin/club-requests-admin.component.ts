import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { Club, ClubStatus } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-requests-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './club-requests-admin.component.html',
  styleUrls: ['./club-requests-admin.component.scss']
})
export class ClubRequestsAdminComponent implements OnInit {
  pendingClubs: Club[] = [];
  loading = false;
  error: string | null = null;
  
  // Modal pour approve/reject
  showReviewModal = false;
  selectedClub: Club | null = null;
  reviewComment = '';
  reviewAction: 'approve' | 'reject' | null = null;
  processing = false;

  ClubStatus = ClubStatus;

  constructor(
    private clubService: ClubService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadPendingClubs();
  }

  loadPendingClubs() {
    this.loading = true;
    this.error = null;

    this.clubService.getPendingClubs().subscribe({
      next: (clubs) => {
        this.pendingClubs = clubs;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading pending clubs:', err);
        this.error = 'Failed to load pending club requests.';
        this.loading = false;
      }
    });
  }

  openReviewModal(club: Club, action: 'approve' | 'reject') {
    this.selectedClub = club;
    this.reviewAction = action;
    this.reviewComment = '';
    this.showReviewModal = true;
  }

  closeReviewModal() {
    this.showReviewModal = false;
    this.selectedClub = null;
    this.reviewAction = null;
    this.reviewComment = '';
  }

  confirmReview() {
    if (!this.selectedClub || !this.reviewAction) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      alert('You must be logged in to review clubs');
      return;
    }

    this.processing = true;

    const reviewMethod = this.reviewAction === 'approve' 
      ? this.clubService.approveClub(this.selectedClub.id!, currentUser.id, this.reviewComment)
      : this.clubService.rejectClub(this.selectedClub.id!, currentUser.id, this.reviewComment);

    reviewMethod.subscribe({
      next: () => {
        this.processing = false;
        this.closeReviewModal();
        this.loadPendingClubs();
        alert(`Club ${this.reviewAction}d successfully!`);
      },
      error: (err) => {
        console.error(`Error ${this.reviewAction}ing club:`, err);
        alert(`Failed to ${this.reviewAction} club. Please try again.`);
        this.processing = false;
      }
    });
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100',
      'BOOK': 'text-green-800 bg-green-100',
      'DRAMA': 'text-orange-800 bg-orange-100',
      'WRITING': 'text-purple-800 bg-purple-100',
      'GRAMMAR': 'text-indigo-800 bg-indigo-100',
      'VOCABULARY': 'text-pink-800 bg-pink-100'
    };
    return classes[category] || 'text-gray-800 bg-gray-100';
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
