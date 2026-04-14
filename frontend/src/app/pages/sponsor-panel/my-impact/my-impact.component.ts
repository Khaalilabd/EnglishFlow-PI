import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { ClubService } from '../../../core/services/club.service';
import { ExpenseService } from '../../../core/services/expense.service';
import { EventService, Event as ClubEvent } from '../../../core/services/event.service';
import { Sponsor } from '../../../core/models/sponsor.model';
import { Club } from '../../../core/models/club.model';
import { Expense } from '../../../core/models/expense.model';

interface ClubImpact {
  sponsor: Sponsor;
  club: Club;
  allocated: number;
  used: number;
  remaining: number;
  usagePercent: number;
  events: ClubEvent[];
}

@Component({
  selector: 'app-my-impact',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-impact.component.html'
})
export class MyImpactComponent implements OnInit {
  loading = true;
  currentUser: any;

  // KPIs
  totalInvested = 0;        // platform contribution
  clubBudgetTotal = 0;      // 30% of platform
  totalAllocatedToClubs = 0; // sum of club sponsorships
  clubBudgetRemaining = 0;
  totalUsed = 0;            // sum of expenses
  clubsSponsored = 0;
  eventsCount = 0;

  // Per-club breakdown
  clubImpacts: ClubImpact[] = [];
  expandedClubs = new Set<number>();

  toggleClub(clubId: number) {
    if (this.expandedClubs.has(clubId)) {
      this.expandedClubs.delete(clubId);
    } else {
      this.expandedClubs.add(clubId);
    }
  }

  isExpanded(clubId: number): boolean {
    return this.expandedClubs.has(clubId);
  }

  constructor(
    private authService: AuthService,
    private sponsorService: SponsorService,
    private clubService: ClubService,
    private expenseService: ExpenseService,
    private eventService: EventService,
    public router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.loadData();
  }

  loadData() {
    const userId = this.currentUser?.id;
    if (!userId) { this.loading = false; return; }

    forkJoin({
      sponsors: this.sponsorService.getSponsorsByUser(userId),
      allEvents: this.eventService.getAllEvents()
    }).subscribe({
      next: ({ sponsors, allEvents }) => {
        // Platform sponsorship = APPROVED without clubId
        const platformSponsor = sponsors.find(s => s.status === 'APPROVED' && !s.clubId);
        const platformContribution = platformSponsor?.contributionAmount || 0;

        // Club budget = 30% of platform contribution
        const clubBudgetTotal = Math.round(platformContribution * 0.30);

        // Club sponsorships = APPROVED with clubId
        const clubSponsors = sponsors.filter(s => s.status === 'APPROVED' && s.clubId);
        const totalAllocatedToClubs = clubSponsors.reduce((sum, s) => sum + (s.contributionAmount || 0), 0);

        this.totalInvested = platformContribution;
        this.clubBudgetTotal = clubBudgetTotal;
        this.totalAllocatedToClubs = totalAllocatedToClubs;
        this.clubBudgetRemaining = Math.max(0, clubBudgetTotal - totalAllocatedToClubs);
        this.clubsSponsored = clubSponsors.length;

        if (clubSponsors.length === 0) { this.loading = false; return; }

        const clubRequests = clubSponsors.map(s =>
          forkJoin({
            club: this.clubService.getClubById(s.clubId!),
            expenses: this.expenseService.getExpensesByClub(s.clubId!)
          }).pipe(
            map(({ club, expenses }) => {
              const sponsorExpenses = expenses.filter(e =>
                e.source === 'SPONSORSHIP' &&
                !e.notes?.includes('SPONSORSHIP_INCOME') &&
                !e.designation?.includes('Sponsorship received from') &&
                !e.designation?.includes('Sponsorship income from')
              );
              const used = sponsorExpenses.reduce((sum, e) => sum + e.amount, 0);
              const allocated = s.contributionAmount || 0;
              const events = allEvents.filter(e => e.clubId === s.clubId);
              return {
                sponsor: s, club, allocated, used,
                remaining: Math.max(0, allocated - used),
                usagePercent: allocated > 0 ? Math.min(100, Math.round((used / allocated) * 100)) : 0,
                events
              } as ClubImpact;
            })
          )
        );

        forkJoin(clubRequests).subscribe({
          next: (impacts) => {
            this.clubImpacts = impacts;
            // Expand all by default
            impacts.forEach(i => this.expandedClubs.add(i.club.id!));
            this.totalUsed = impacts.reduce((sum, i) => sum + i.used, 0);
            this.eventsCount = [...new Set(impacts.flatMap(i => i.events.map(e => e.id)))].length;
            this.loading = false;
          },
          error: () => { this.loading = false; }
        });
      },
      error: () => { this.loading = false; }
    });
  }

  get totalRemaining(): number {
    return Math.max(0, this.clubBudgetTotal - this.totalAllocatedToClubs);
  }

  get globalUsagePercent(): number {
    return this.clubBudgetTotal > 0
      ? Math.min(100, Math.round((this.totalAllocatedToClubs / this.clubBudgetTotal) * 100))
      : 0;
  }

  isOnline(event: ClubEvent): boolean {
    return event.format === 'ONLINE';
  }

  joinLive(eventId: number, clubId: number) {
    this.router.navigate(['/live', eventId], {
      queryParams: { ghost: true, returnTo: '/sponsor-panel/my-impact' }
    });
  }

  getEventStatusClass(status: string): string {
    const m: Record<string, string> = {
      APPROVED: 'bg-green-100 text-green-700',
      PENDING: 'bg-yellow-100 text-yellow-700',
      REJECTED: 'bg-red-100 text-red-700'
    };
    return m[status] || 'bg-gray-100 text-gray-600';
  }

  getCategoryIcon(cat: string): string {
    const m: Record<string, string> = {
      CONVERSATION:'💬',BOOK:'📚',DRAMA:'🎭',WRITING:'✍️',GRAMMAR:'📝',
      VOCABULARY:'🔤',READING:'📖',LISTENING:'🎧',SPEAKING:'🎤',
      PRONUNCIATION:'🗣️',BUSINESS:'💼',ACADEMIC:'🎓'
    };
    return m[cat] || '🏛️';
  }
}
