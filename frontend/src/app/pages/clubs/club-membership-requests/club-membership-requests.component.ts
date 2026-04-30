import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MembershipRequest } from '../../../core/models/club.model';
import { MembershipRequestService } from '../../../core/services/membership-request.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ClubWebSocketService } from '../../../services/club-websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-club-membership-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './club-membership-requests.component.html',
  styleUrls: ['./club-membership-requests.component.scss'],
})
export class ClubMembershipRequestsComponent implements OnInit, OnDestroy {
  @Input() clubId!: number;

  requests: MembershipRequest[] = [];
  loading = false;
  currentUserId: number | null = null;
  selectedRequest: MembershipRequest | null = null;

  // Reject modal
  rejectingRequest: MembershipRequest | null = null;
  rejectComment = '';

  private wsSubscription?: Subscription;

  constructor(
    private readonly requestService: MembershipRequestService,
    private readonly authService: AuthService,
    private readonly notificationService: NotificationService,
    private readonly clubWebsocket: ClubWebSocketService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadRequests();
    this.subscribeToWebSocket();
  }

  ngOnDestroy(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

  loadCurrentUser(): void {
    this.authService.currentUser$.subscribe((user) => {
      if (user && user.id) {
        this.currentUserId = user.id;
      }
    });
  }

  loadRequests(): void {
    if (!this.clubId) return;
    this.loading = true;
    this.requestService.getPendingRequestsForClub(this.clubId).subscribe({
      next: (requests) => {
        this.requests = requests;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading membership requests:', err);
        this.loading = false;
      },
    });
  }

  subscribeToWebSocket(): void {
    if (!this.clubId) return;
    this.wsSubscription = this.clubWebsocket.subscribeToClub(this.clubId).subscribe({
      next: (notification: any) => {
        if (notification && notification.type === 'MEMBERSHIP_REQUEST') {
          this.loadRequests();
        }
      },
      error: (err: any) => console.error('WebSocket error:', err),
    });
  }

  viewRequest(request: MembershipRequest): void {
    this.selectedRequest = request;
  }

  closeModal(): void {
    this.selectedRequest = null;
  }

  approveRequest(request: MembershipRequest): void {
    if (!this.currentUserId || !request.id) return;
    this.requestService.approveRequest(request.id, this.currentUserId).subscribe({
      next: () => {
        this.notificationService.success('Request Approved', `${request.userName || 'User'} has been approved and added to the club.`);
        this.loadRequests();
      },
      error: (err) => {
        this.notificationService.error('Approval Failed', err.error?.message || 'Failed to approve request.');
      },
    });
  }

  openRejectModal(request: MembershipRequest): void {
    this.rejectingRequest = request;
    this.rejectComment = '';
  }

  closeRejectModal(): void {
    this.rejectingRequest = null;
    this.rejectComment = '';
  }

  confirmReject(): void {
    if (!this.currentUserId || !this.rejectingRequest?.id) return;
    this.requestService
      .rejectRequest(this.rejectingRequest.id, this.currentUserId, this.rejectComment || undefined)
      .subscribe({
        next: () => {
          this.notificationService.success('Request Rejected', `The request from ${this.rejectingRequest?.userName || 'user'} has been rejected.`);
          this.closeRejectModal();
          this.loadRequests();
        },
        error: (err) => {
          this.notificationService.error('Rejection Failed', err.error?.message || 'Failed to reject request.');
        },
      });
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
