import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SponsorService } from '../../core/services/sponsor.service';
import { CreateSponsorRequest, SponsorLevel } from '../../core/models/sponsor.model';
import { AuthService } from '../../core/services/auth.service';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';

@Component({
  selector: 'app-become-sponsor',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, FrontofficeUserDropdownComponent, FrontofficeNotificationDropdownComponent],
  templateUrl: './become-sponsor.component.html',
  styleUrls: ['./become-sponsor.component.scss']
})
export class BecomeSponsorComponent {
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

  loading = false;
  success = false;
  error: string | null = null;
  mobileMenuOpen = false;
  SponsorLevel = SponsorLevel;

  constructor(
    private sponsorService: SponsorService,
    public authService: AuthService
  ) {}

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  onLogoSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) { this.error = 'Please select an image file'; return; }
    if (file.size > 5 * 1024 * 1024) { this.error = 'Image size must be less than 5MB'; return; }
    const reader = new FileReader();
    reader.onload = (e: any) => { this.sponsor.logo = e.target.result; };
    reader.readAsDataURL(file);
  }

  removeLogo() {
    this.sponsor.logo = '';
  }

  getAutoLevel(): string {
    const amount = Number(this.sponsor.contributionAmount) || 0;
    if (amount >= 1000) return '🥇 Gold';
    if (amount >= 500) return '🥈 Silver';
    return '🥉 Bronze';
  }

  getAutoLevelValue(): SponsorLevel {
    const amount = Number(this.sponsor.contributionAmount) || 0;
    if (amount >= 1000) return SponsorLevel.GOLD;
    if (amount >= 500) return SponsorLevel.SILVER;
    return SponsorLevel.BRONZE;
  }

  submit() {
    if (!this.sponsor.name || !this.sponsor.contactEmail) {
      this.error = 'Name and email are required.';
      return;
    }
    this.loading = true;
    this.error = null;
    this.sponsor.contributionAmount = Number(this.sponsor.contributionAmount);
    this.sponsor.level = this.getAutoLevelValue();

    this.sponsorService.createSponsor(this.sponsor).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        this.sponsor = { name: '', description: '', logo: '', website: '', contactEmail: '', contactPhone: '', level: SponsorLevel.BRONZE, contributionAmount: 0 };
      },
      error: () => {
        this.error = 'Failed to submit. Please try again.';
        this.loading = false;
      }
    });
  }
}
