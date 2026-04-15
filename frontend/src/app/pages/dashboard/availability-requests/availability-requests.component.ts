import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvailabilityModificationRequestService, AvailabilityModificationRequest } from '../../../core/services/availability-modification-request.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-availability-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './availability-requests.component.html',
  styleUrls: ['./availability-requests.component.scss']
})
export class AvailabilityRequestsComponent implements OnInit {
  requests: AvailabilityModificationRequest[] = [];
  filteredRequests: AvailabilityModificationRequest[] = [];
  loading = false;
  selectedRequest: AvailabilityModificationRequest | null = null;
  showReviewModal = false;
  reviewComment = '';
  reviewAction: 'approve' | 'reject' = 'approve';
  filterStatus: string = 'all';

  constructor(
    private requestService: AvailabilityModificationRequestService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading = true;
    this.requestService.getAllRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.applyFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading requests:', error);
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    if (this.filterStatus === 'all') {
      this.filteredRequests = this.requests;
    } else {
      this.filteredRequests = this.requests.filter(r => r.status === this.filterStatus.toUpperCase());
    }
  }

  onFilterChange(): void {
    this.applyFilter();
  }

  openReviewModal(request: AvailabilityModificationRequest, action: 'approve' | 'reject'): void {
    this.selectedRequest = request;
    this.reviewAction = action;
    this.reviewComment = '';
    this.showReviewModal = true;
  }

  closeReviewModal(): void {
    this.showReviewModal = false;
    this.selectedRequest = null;
    this.reviewComment = '';
  }

  submitReview(): void {
    if (!this.selectedRequest) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      alert('User not authenticated');
      return;
    }

    const reviewerId = currentUser.id;
    const reviewerName = `${currentUser.firstName} ${currentUser.lastName}`;

    if (this.reviewAction === 'approve') {
      this.requestService.approveRequest(
        this.selectedRequest.id!,
        reviewerId,
        reviewerName,
        this.reviewComment
      ).subscribe({
        next: () => {
          alert('Request approved successfully!');
          this.closeReviewModal();
          this.loadRequests();
        },
        error: (error) => {
          console.error('Error approving request:', error);
          alert('Failed to approve request');
        }
      });
    } else {
      this.requestService.rejectRequest(
        this.selectedRequest.id!,
        reviewerId,
        reviewerName,
        this.reviewComment
      ).subscribe({
        next: () => {
          alert('Request rejected successfully!');
          this.closeReviewModal();
          this.loadRequests();
        },
        error: (error) => {
          console.error('Error rejecting request:', error);
          alert('Failed to reject request');
        }
      });
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(date: Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
  }

  parseAvailability(jsonString: string | undefined): any {
    if (!jsonString) return null;
    try {
      return JSON.parse(jsonString);
    } catch (e) {
      console.error('Error parsing availability:', e);
      return null;
    }
  }
}
