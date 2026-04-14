import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { Sponsor } from '../../../core/models/sponsor.model';

interface Level {
  name: string;
  emoji: string;
  min: number;
  max: number | null;
  color: string;
  bgColor: string;
  borderColor: string;
  perks: string[];
}

@Component({
  selector: 'app-sponsorship-level',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsorship-level.component.html'
})
export class SponsorshipLevelComponent implements OnInit {
  loading = true;
  currentUser: any;
  sponsors: Sponsor[] = [];
  totalContribution = 0;

  readonly levels: Level[] = [
    {
      name: 'Bronze',
      emoji: '🥉',
      min: 0,
      max: 499,
      color: 'text-amber-700',
      bgColor: 'bg-amber-50',
      borderColor: 'border-amber-200',
      perks: [
        'Access to sponsor a club',
        'Badge on sponsored events',
        'Monthly activity report',
        'Sponsor profile page'
      ]
    },
    {
      name: 'Silver',
      emoji: '🥈',
      min: 500,
      max: 999,
      color: 'text-gray-600',
      bgColor: 'bg-gray-50',
      borderColor: 'border-gray-300',
      perks: [
        'All Bronze perks',
        'Visibility on public sponsor page',
        'Priority club access',
        'Quarterly impact report',
        'Logo on club events'
      ]
    },
    {
      name: 'Gold',
      emoji: '🥇',
      min: 1000,
      max: null,
      color: 'text-yellow-600',
      bgColor: 'bg-yellow-50',
      borderColor: 'border-yellow-300',
      perks: [
        'All Silver perks',
        'Featured on homepage',
        'Dedicated account manager',
        'Custom sponsorship packages',
        'VIP event invitations',
        'Annual recognition award'
      ]
    }
  ];

  constructor(
    private authService: AuthService,
    private sponsorService: SponsorService,
    public router: Router
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.loadData();
  }

  loadData() {
    const userId = this.currentUser?.id;
    if (!userId) { this.loading = false; return; }

    this.sponsorService.getSponsorsByUser(userId).subscribe({
      next: (sponsors) => {
        this.sponsors = sponsors;
        // Level is based only on the platform contribution (no clubId)
        const platformSponsor = sponsors.find(s => s.status === 'APPROVED' && !s.clubId);
        this.totalContribution = platformSponsor?.contributionAmount || 0;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  get currentLevel(): Level {
    if (this.totalContribution >= 1000) return this.levels[2];
    if (this.totalContribution >= 500) return this.levels[1];
    return this.levels[0];
  }

  get nextLevel(): Level | null {
    const idx = this.levels.indexOf(this.currentLevel);
    return idx < this.levels.length - 1 ? this.levels[idx + 1] : null;
  }

  get progressToNext(): number {
    if (!this.nextLevel) return 100;
    const current = this.currentLevel;
    const range = (this.nextLevel.min) - current.min;
    const progress = this.totalContribution - current.min;
    return Math.min(100, Math.round((progress / range) * 100));
  }

  get amountToNext(): number {
    if (!this.nextLevel) return 0;
    return Math.max(0, this.nextLevel.min - this.totalContribution);
  }

  isCurrentLevel(level: Level): boolean {
    return level.name === this.currentLevel.name;
  }

  isUnlocked(level: Level): boolean {
    return this.totalContribution >= level.min;
  }

  getStatusClass(status: string): string {
    const m: Record<string, string> = {
      APPROVED: 'bg-green-100 text-green-700',
      PENDING: 'bg-yellow-100 text-yellow-700',
      REJECTED: 'bg-red-100 text-red-700'
    };
    return m[status] || 'bg-gray-100 text-gray-600';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
