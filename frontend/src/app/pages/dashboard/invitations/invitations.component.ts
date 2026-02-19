import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { InvitationService, InvitationRequest, InvitationResponse } from '../../../core/services/invitation.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-invitations',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './invitations.component.html',
  styleUrls: ['./invitations.component.scss']
})
export class InvitationsComponent implements OnInit {
  invitations: InvitationResponse[] = [];
  filteredInvitations: InvitationResponse[] = [];
  loading = false;
  showInviteModal = false;
  inviteForm: FormGroup;
  
  // Filters
  searchTerm = '';
  selectedStatus: 'ALL' | 'PENDING' | 'USED' | 'EXPIRED' = 'ALL';
  selectedRole: 'ALL' | 'TUTOR' | 'ACADEMIC_OFFICE_AFFAIR' = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor(
    private invitationService: InvitationService,
    private fb: FormBuilder,
    private toastService: ToastService
  ) {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['TUTOR', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadInvitations();
  }

  loadInvitations(): void {
    this.loading = true;
    this.invitationService.getAllInvitations().subscribe({
      next: (data) => {
        this.invitations = data.sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading invitations:', error);
        this.toastService.error('Failed to load invitations');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.invitations];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(inv =>
        inv.email.toLowerCase().includes(term) ||
        inv.role.toLowerCase().includes(term)
      );
    }

    // Status filter
    if (this.selectedStatus !== 'ALL') {
      if (this.selectedStatus === 'PENDING') {
        filtered = filtered.filter(inv => 
          !inv.used && !this.invitationService.isExpired(inv.expiryDate)
        );
      } else if (this.selectedStatus === 'USED') {
        filtered = filtered.filter(inv => inv.used);
      } else if (this.selectedStatus === 'EXPIRED') {
        filtered = filtered.filter(inv => 
          !inv.used && this.invitationService.isExpired(inv.expiryDate)
        );
      }
    }

    // Role filter
    if (this.selectedRole !== 'ALL') {
      filtered = filtered.filter(inv => inv.role === this.selectedRole);
    }

    this.filteredInvitations = filtered;
    this.totalPages = Math.ceil(this.filteredInvitations.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  get paginatedInvitations(): InvitationResponse[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredInvitations.slice(start, end);
  }

  openInviteModal(): void {
    this.inviteForm.reset({ role: 'TUTOR' });
    this.showInviteModal = true;
  }

  closeInviteModal(): void {
    this.showInviteModal = false;
    this.inviteForm.reset();
  }

  sendInvitation(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }

    const request: InvitationRequest = this.inviteForm.value;
    
    this.invitationService.sendInvitation(request).subscribe({
      next: (response) => {
        this.toastService.success(`Invitation sent to ${response.email}`);
        this.invitations.unshift(response);
        this.applyFilters();
        this.closeInviteModal();
      },
      error: (error) => {
        console.error('Error sending invitation:', error);
        const message = error.error?.message || 'Failed to send invitation';
        this.toastService.error(message);
      }
    });
  }

  resendInvitation(invitation: InvitationResponse): void {
    if (confirm(`Resend invitation to ${invitation.email}?`)) {
      this.invitationService.resendInvitation(invitation.id).subscribe({
        next: (updated) => {
          this.toastService.success(`Invitation resent to ${invitation.email}`);
          const index = this.invitations.findIndex(inv => inv.id === invitation.id);
          if (index !== -1) {
            this.invitations[index] = updated;
            this.applyFilters();
          }
        },
        error: (error) => {
          console.error('Error resending invitation:', error);
          this.toastService.error('Failed to resend invitation');
        }
      });
    }
  }

  cancelInvitation(invitation: InvitationResponse): void {
    if (confirm(`Cancel invitation for ${invitation.email}? This action cannot be undone.`)) {
      this.invitationService.cancelInvitation(invitation.id).subscribe({
        next: () => {
          this.toastService.success(`Invitation cancelled for ${invitation.email}`);
          this.invitations = this.invitations.filter(inv => inv.id !== invitation.id);
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error cancelling invitation:', error);
          this.toastService.error('Failed to cancel invitation');
        }
      });
    }
  }

  cleanupExpired(): void {
    if (confirm('Remove all expired invitations? This action cannot be undone.')) {
      this.invitationService.cleanupExpiredInvitations().subscribe({
        next: () => {
          this.toastService.success('Expired invitations cleaned up');
          this.loadInvitations();
        },
        error: (error) => {
          console.error('Error cleaning up invitations:', error);
          this.toastService.error('Failed to cleanup expired invitations');
        }
      });
    }
  }

  copyInvitationLink(invitation: InvitationResponse): void {
    const link = `${window.location.origin}/accept-invitation?token=${invitation.token}`;
    navigator.clipboard.writeText(link).then(() => {
      this.toastService.success('Invitation link copied to clipboard');
    }).catch(() => {
      this.toastService.error('Failed to copy link');
    });
  }

  getStatusBadgeClass(invitation: InvitationResponse): string {
    if (invitation.used) {
      return 'bg-green-100 text-green-800';
    } else if (this.invitationService.isExpired(invitation.expiryDate)) {
      return 'bg-red-100 text-red-800';
    } else {
      return 'bg-yellow-100 text-yellow-800';
    }
  }

  getStatusText(invitation: InvitationResponse): string {
    if (invitation.used) {
      return 'Accepted';
    } else if (this.invitationService.isExpired(invitation.expiryDate)) {
      return 'Expired';
    } else {
      const days = this.invitationService.getDaysUntilExpiry(invitation.expiryDate);
      return `Pending (${days}d left)`;
    }
  }

  canResend(invitation: InvitationResponse): boolean {
    return !invitation.used;
  }

  canCancel(invitation: InvitationResponse): boolean {
    return !invitation.used;
  }

  formatRoleName(role: string): string {
    return this.invitationService.formatRoleName(role);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Pagination
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // Expose Math for template
  get Math() {
    return Math;
  }

  // Statistics
  get totalInvitations(): number {
    return this.invitations.length;
  }

  get pendingCount(): number {
    return this.invitations.filter(inv => 
      !inv.used && !this.invitationService.isExpired(inv.expiryDate)
    ).length;
  }

  get acceptedCount(): number {
    return this.invitations.filter(inv => inv.used).length;
  }

  get expiredCount(): number {
    return this.invitations.filter(inv => 
      !inv.used && this.invitationService.isExpired(inv.expiryDate)
    ).length;
  }
}
