import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { SponsorService } from '../../../core/services/sponsor.service';
import { Sponsor, SponsorLevel } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-sponsor-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsor-detail.component.html',
  styleUrls: ['./sponsor-detail.component.scss']
})
export class SponsorDetailComponent implements OnInit {
  sponsor: Sponsor | null = null;
  loading = true;

  constructor(
    private sponsorService: SponsorService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadSponsor(id);
  }

  loadSponsor(id: number) {
    this.loading = true;
    this.sponsorService.getSponsorById(id).subscribe({
      next: (sponsor) => {
        this.sponsor = sponsor;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading sponsor:', err);
        this.notificationService.error('Error', 'Failed to load sponsor');
        this.router.navigate(['/dashboard/sponsors']);
      }
    });
  }

  getLevelBadgeClass(level?: SponsorLevel): string {
    const classes: { [key: string]: string } = {
      'GOLD': 'gold',
      'SILVER': 'silver',
      'BRONZE': 'bronze'
    };
    return level ? classes[level] : classes['BRONZE'];
  }

  editSponsor() {
    this.router.navigate(['/dashboard/sponsors/edit', this.sponsor?.id]);
  }

  deleteSponsor() {
    if (this.sponsor && confirm(`Are you sure you want to delete ${this.sponsor.name}?`)) {
      this.sponsorService.deleteSponsor(this.sponsor.id!).subscribe({
        next: () => {
          this.notificationService.success('Success', 'Sponsor deleted successfully');
          this.router.navigate(['/dashboard/sponsors']);
        },
        error: (err) => {
          console.error('Error deleting sponsor:', err);
          this.notificationService.error('Error', 'Failed to delete sponsor');
        }
      });
    }
  }

  goBack() {
    this.router.navigate(['/dashboard/sponsors']);
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
