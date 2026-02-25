import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { MemberService } from '../../../core/services/member.service';
import { AuthService } from '../../../core/services/auth.service';
import { ClubUpdateRequestService, ClubUpdateRequest } from '../../../core/services/club-update-request.service';
import { Club, Member } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './club-detail.component.html',
  styleUrls: ['./club-detail.component.scss']
})
export class ClubDetailComponent implements OnInit {
  club: Club | null = null;
  members: Member[] = [];
  loading = false;
  loadingMembers = false;
  error: string | null = null;
  clubId!: number;
  
  // Approval system
  pendingRequests: ClubUpdateRequest[] = [];
  loadingRequests = false;
  currentUserId: number | null = null;
  currentUserRank: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private memberService: MemberService,
    private authService: AuthService,
    private updateRequestService: ClubUpdateRequestService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.clubId = +params['id'];
      this.loadCurrentUser();
      this.loadClub();
      this.loadMembers();
      // Don't load pending requests here - wait for user rank to be loaded
    });
  }

  loadCurrentUser() {
    this.authService.currentUser$.subscribe(user => {
      if (user && user.id) {
        this.currentUserId = user.id;
        this.loadUserRank();
      }
    });
  }

  loadUserRank() {
    if (!this.currentUserId) return;
    
    console.log('Loading rank for user', this.currentUserId, 'in club', this.clubId);
    this.memberService.getUserMembershipInClub(this.clubId, this.currentUserId).subscribe({
      next: (member: any) => {
        this.currentUserRank = member?.rank || null;
        console.log('User rank loaded:', this.currentUserRank);
        console.log('Can approve requests:', this.canApproveRequests());
        
        // Reload pending requests after rank is loaded
        this.loadPendingRequests();
      },
      error: (err: any) => {
        console.log('User is not a member of this club', err);
        this.currentUserRank = null;
      }
    });
  }

  loadClub() {
    this.loading = true;
    this.error = null;

    this.clubService.getClubById(this.clubId).subscribe({
      next: (club) => {
        this.club = club;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading club:', err);
        this.error = 'Failed to load club details.';
        this.loading = false;
      }
    });
  }

  loadMembers() {
    this.loadingMembers = true;

    this.clubService.getClubMembers(this.clubId).subscribe({
      next: (members) => {
        this.members = members;
        this.loadingMembers = false;
      },
      error: (err) => {
        console.error('Error loading members:', err);
        this.loadingMembers = false;
      }
    });
  }

  deleteClub() {
    if (confirm('Are you sure you want to delete this club? This action cannot be undone.')) {
      this.clubService.deleteClub(this.clubId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/clubs']);
        },
        error: (err) => {
          console.error('Error deleting club:', err);
          alert('Failed to delete club. Please try again.');
        }
      });
    }
  }

  removeMember(userId: number) {
    if (confirm('Are you sure you want to remove this member?')) {
      this.clubService.leaveClub(this.clubId, userId).subscribe({
        next: () => {
          this.loadMembers();
          this.loadClub();
        },
        error: (err) => {
          console.error('Error removing member:', err);
          alert('Failed to remove member. Please try again.');
        }
      });
    }
  }

  showAddMemberModal() {
    const userId = prompt('Enter user ID to add:');
    if (userId) {
      this.clubService.joinClub(this.clubId, { userId: +userId }).subscribe({
        next: () => {
          this.loadMembers();
          this.loadClub();
        },
        error: (err) => {
          console.error('Error adding member:', err);
          alert('Failed to add member. Please try again.');
        }
      });
    }
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100',
      'BOOK': 'text-green-800 bg-green-100',
      'DRAMA': 'text-orange-800 bg-orange-100',
      'WRITING': 'text-purple-800 bg-purple-100'
    };
    return classes[category] || 'text-gray-800 bg-gray-100';
  }

  // Approval system methods
  loadPendingRequests() {
    this.loadingRequests = true;
    console.log('Loading pending requests for club', this.clubId);
    this.updateRequestService.getPendingRequestsForClub(this.clubId).subscribe({
      next: (requests) => {
        this.pendingRequests = requests;
        console.log('Pending requests loaded:', requests.length, 'requests');
        console.log('Current user rank:', this.currentUserRank);
        console.log('Can approve:', this.canApproveRequests());
        this.loadingRequests = false;
      },
      error: (err) => {
        console.error('Error loading pending requests:', err);
        this.loadingRequests = false;
      }
    });
  }

  canApproveRequests(): boolean {
    return this.currentUserRank === 'VICE_PRESIDENT' || this.currentUserRank === 'SECRETARY';
  }

  approveRequest(requestId: number) {
    if (!this.currentUserId) {
      alert('Vous devez être connecté pour approuver');
      return;
    }

    this.updateRequestService.approveRequest(requestId, this.currentUserId).subscribe({
      next: (updatedRequest) => {
        if (updatedRequest.status === 'APPROVED') {
          alert('Demande approuvée et modifications appliquées !');
          this.loadClub(); // Reload club to show updated info
        } else {
          alert('Votre approbation a été enregistrée. En attente de l\'autre approbation.');
        }
        this.loadPendingRequests();
      },
      error: (err) => {
        console.error('Error approving request:', err);
        alert(err.error?.message || 'Erreur lors de l\'approbation');
      }
    });
  }

  rejectRequest(requestId: number) {
    if (!this.currentUserId) {
      alert('Vous devez être connecté pour rejeter');
      return;
    }

    if (!confirm('Êtes-vous sûr de vouloir rejeter cette demande ?')) {
      return;
    }

    this.updateRequestService.rejectRequest(requestId, this.currentUserId).subscribe({
      next: () => {
        alert('Demande rejetée');
        this.loadPendingRequests();
      },
      error: (err) => {
        console.error('Error rejecting request:', err);
        alert(err.error?.message || 'Erreur lors du rejet');
      }
    });
  }

  getApprovalStatus(request: ClubUpdateRequest): string {
    const approvals = [];
    if (request.vicePresidentApproved) approvals.push('Vice-Président');
    if (request.secretaryApproved) approvals.push('Secrétaire');
    
    if (approvals.length === 0) return 'En attente des deux approbations';
    if (approvals.length === 1) return `Approuvé par: ${approvals[0]}`;
    return 'Approuvé par les deux';
  }
}
