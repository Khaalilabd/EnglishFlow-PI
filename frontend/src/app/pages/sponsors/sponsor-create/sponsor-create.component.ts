import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SponsorService } from '../../../core/services/sponsor.service';
import { CreateSponsorRequest, SponsorLevel } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-sponsor-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sponsor-create.component.html',
  styleUrl: './sponsor-create.component.scss'
})
export class SponsorCreateComponent {
  sponsor: CreateSponsorRequest = {
    name: '',
    description: '',
    logo: '',
    website: '',
    contactEmail: '',
    contactPhone: '',
    level: SponsorLevel.BRONZE,
    contributionAmount: 0
  };

  SponsorLevel = SponsorLevel;
  loading = false;

  constructor(
    private sponsorService: SponsorService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  onLogoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (!file.type.startsWith('image/')) {
        this.notificationService.warning('Invalid File', 'Please select an image file');
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.notificationService.warning('File Too Large', 'Image size must be less than 5MB');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.sponsor.logo = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeLogo() {
    this.sponsor.logo = '';
  }

  saveSponsor() {
    if (!this.sponsor.name) {
      this.notificationService.warning('Missing Fields', 'Name is required');
      return;
    }

    // Ensure contributionAmount is a proper number
    const rawAmount = Number(this.sponsor.contributionAmount);
    this.sponsor.contributionAmount = rawAmount;

    this.loading = true;
    this.sponsorService.createSponsor(this.sponsor).subscribe({
      next: () => {
        this.notificationService.success('Success', 'Sponsor created successfully');
        this.router.navigate(['/dashboard/sponsors']);
      },
      error: (err) => {
        console.error('Error creating sponsor:', err);
        this.notificationService.error('Error', 'Failed to create sponsor');
        this.loading = false;
      }
    });
  }

  cancel() {
    this.router.navigate(['/dashboard/sponsors']);
  }
}
