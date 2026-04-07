import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MembershipRequest } from '../../../core/models/club.model';
import { MembershipRequestService } from '../../../core/services/membership-request.service';
import { AuthService } from '../../../core/services/auth.service';
import { ClubWebSocketService } from '../../../services/club-websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-club-membership-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './club-membership-requests.component.html',
  styleUrls: ['./club-membership-requests.component.scss']
})
export class ClubMembershipRequestsComponent implements OnInit, OnDestroy {
  @Input() clubId!: number;
  
  requests: MembershipRequest[] = [];
  loading = false;
  currentUserId: number | null = null;
  selectedRequest: MembershipRequest | null = null;
  private wsSubscription?: Subscription;

  constructor(
    private requestService: MembershipRequestService,
    private authService: AuthService,
    private clubWebsocket: ClubWebSocketService
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
    this.authService.currentUser$.subscribe(user => {
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
      }
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
      error: (err: any) => console.error('WebSocket error:', err)
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

    if (confirm(`Approuver la demande de ${request.userName || 'cet utilisateur'} ?`)) {
      this.requestService.approveRequest(request.id, this.currentUserId).subscribe({
        next: () => {
          alert('Demande approuvée avec succès !');
          this.loadRequests();
        },
        error: (err) => {
          console.error('Error approving request:', err);
          alert(err.error?.message || 'Erreur lors de l\'approbation');
        }
      });
    }
  }

  rejectRequest(request: MembershipRequest): void {
    if (!this.currentUserId || !request.id) return;

    const comment = prompt(`Rejeter la demande de ${request.userName || 'cet utilisateur'} ?\nRaison (optionnelle):`);
    if (comment !== null) {
      this.requestService.rejectRequest(request.id, this.currentUserId, comment || undefined).subscribe({
        next: () => {
          alert('Demande rejetée');
          this.loadRequests();
        },
        error: (err) => {
          console.error('Error rejecting request:', err);
          alert(err.error?.message || 'Erreur lors du rejet');
        }
      });
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
