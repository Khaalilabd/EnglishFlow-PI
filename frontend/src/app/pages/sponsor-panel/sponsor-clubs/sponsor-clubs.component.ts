import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { AuthService } from '../../../core/services/auth.service';
import { Club, ClubCategory } from '../../../core/models/club.model';
import { Sponsor } from '../../../core/models/sponsor.model';

@Component({
  selector: 'app-sponsor-clubs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sponsor-clubs.component.html'
})
export class SponsorClubsComponent implements OnInit {
  allClubs: Club[] = [];
  clubs: Club[] = [];
  myRequests: Sponsor[] = [];
  loading = false;
  submitting = false;
  success = false;
  error: string | null = null;

  // Search / Filter / Sort
  searchQuery = '';
  selectedCategory: ClubCategory | '' = '';
  sortBy: 'name' | 'members' | 'fee' = 'name';
  sortDir: 'asc' | 'desc' = 'asc';

  readonly categories = Object.values(ClubCategory);

  // Sponsor request modal
  showModal = false;
  selectedClub: Club | null = null;
  amount = 0;
  message = '';
  sponsorProfile: Sponsor | null = null;

  currentUser: any;

  // ── Budget helpers ────────────────────────────────────────────────────────

  get totalClubBudget(): number {
    if (!this.sponsorProfile?.contributionAmount) return 0;
    return Math.round(this.sponsorProfile.contributionAmount * 0.30);
  }

  get alreadyCommitted(): number {
    return this.myRequests
      .filter(r => r.clubId && r.status !== 'REJECTED')
      .reduce((sum, r) => sum + (r.contributionAmount || 0), 0);
  }

  get remainingBudget(): number {
    return Math.max(0, this.totalClubBudget - this.alreadyCommitted);
  }

  /** Approved club requests — clubs the sponsor is actively sponsoring */
  get approvedClubRequests(): Sponsor[] {
    return this.myRequests.filter(r => r.clubId && r.status === 'APPROVED');
  }

  constructor(
    private clubService: ClubService,
    private sponsorService: SponsorService,
    private authService: AuthService,
    public router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.loadClubs();
    this.loadMyRequests();
    this.loadSponsorProfile();
  }

  loadSponsorProfile() {
    if (!this.currentUser?.id) return;
    this.sponsorService.getSponsorsByUser(this.currentUser.id).subscribe({
      next: (sponsors) => {
        this.sponsorProfile = sponsors.find(s => s.status === 'APPROVED' && !s.clubId) || null;
      },
      error: () => {}
    });
  }

  loadClubs() {
    this.loading = true;
    this.clubService.getApprovedClubs().subscribe({
      next: (clubs) => {
        this.allClubs = clubs;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  loadMyRequests() {
    if (!this.currentUser?.id) return;
    this.sponsorService.getSponsorsByUser(this.currentUser.id).subscribe({
      next: (requests) => { this.myRequests = requests; },
      error: () => {}
    });
  }

  // ── Filters ───────────────────────────────────────────────────────────────

  applyFilters() {
    let result = [...this.allClubs];
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(c =>
        c.name.toLowerCase().includes(q) ||
        (c.description || '').toLowerCase().includes(q)
      );
    }
    if (this.selectedCategory) {
      result = result.filter(c => c.category === this.selectedCategory);
    }
    result.sort((a, b) => {
      let valA: any, valB: any;
      if (this.sortBy === 'name') { valA = a.name.toLowerCase(); valB = b.name.toLowerCase(); }
      else if (this.sortBy === 'members') { valA = a.currentMembersCount ?? 0; valB = b.currentMembersCount ?? 0; }
      else { valA = a.registrationFee ?? 0; valB = b.registrationFee ?? 0; }
      const cmp = valA < valB ? -1 : valA > valB ? 1 : 0;
      return this.sortDir === 'asc' ? cmp : -cmp;
    });
    this.clubs = result;
  }

  toggleSort(field: 'name' | 'members' | 'fee') {
    if (this.sortBy === field) { this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc'; }
    else { this.sortBy = field; this.sortDir = 'asc'; }
    this.applyFilters();
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedCategory = '';
    this.sortBy = 'name';
    this.sortDir = 'asc';
    this.applyFilters();
  }

  get hasActiveFilters(): boolean {
    return !!(this.searchQuery || this.selectedCategory);
  }

  // ── Request modal ─────────────────────────────────────────────────────────

  openModal(club: Club) {
    this.selectedClub = club;
    this.amount = this.remainingBudget;
    this.message = '';
    this.error = null;
    this.success = false;
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.selectedClub = null;
  }

  getLevel(): string {
    if (this.amount >= 1000) return '🥇 Gold';
    if (this.amount >= 500)  return '🥈 Silver';
    return '🥉 Bronze';
  }

  hasAlreadyRequested(clubId: number): boolean {
    return this.myRequests.some(r => r.clubId === clubId && r.status !== 'REJECTED');
  }

  getRequestForClub(clubId: number): Sponsor | undefined {
    return this.myRequests.find(r => r.clubId === clubId && r.status !== 'REJECTED');
  }

  submitRequest() {
    if (!this.selectedClub || !this.currentUser) return;
    if (this.amount <= 0) { this.error = 'Please enter a valid amount.'; return; }
    if (this.amount > this.remainingBudget) {
      this.error = `Amount exceeds your remaining budget of ${this.remainingBudget} DT.`;
      return;
    }
    this.submitting = true;
    this.error = null;
    const payload = {
      name: this.currentUser.firstName + ' ' + this.currentUser.lastName,
      description: this.message || `Sponsorship for club: ${this.selectedClub.name}`,
      contributionAmount: this.amount,
      contactEmail: this.currentUser.email,
      userId: this.currentUser.id,
      applicantFirstName: this.currentUser.firstName,
      applicantLastName: this.currentUser.lastName,
      clubId: this.selectedClub.id!,
      clubName: this.selectedClub.name
    };
    this.sponsorService.createClubSponsorRequest(payload).subscribe({
      next: () => {
        this.submitting = false;
        this.success = true;
        this.showModal = false;
        this.loadMyRequests();
      },
      error: (err) => {
        this.submitting = false;
        this.error = err?.error?.message || 'Failed to submit request. Please try again.';
      }
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING:  'bg-yellow-100 text-yellow-700',
      APPROVED: 'bg-green-100 text-green-700',
      REJECTED: 'bg-red-100 text-red-700'
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  getStatusIcon(status: string): string {
    const map: Record<string, string> = { PENDING: '⏳', APPROVED: '✅', REJECTED: '❌' };
    return map[status] || '❓';
  }

  getCategoryIcon(cat: string): string {
    const map: Record<string, string> = {
      CONVERSATION: '💬', BOOK: '📚', DRAMA: '🎭', WRITING: '✍️',
      GRAMMAR: '📝', VOCABULARY: '🔤', READING: '📖', LISTENING: '🎧',
      SPEAKING: '🎤', PRONUNCIATION: '🗣️', BUSINESS: '💼', ACADEMIC: '🎓'
    };
    return map[cat] || '🏛️';
  }
}
