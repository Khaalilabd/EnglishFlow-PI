import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { SponsorService } from '../../../core/services/sponsor.service';
import { CreateSponsorRequest, SponsorLevel, Sponsor } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-sponsor-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sponsor-edit.component.html',
  styleUrl: './sponsor-edit.component.scss'
})
export class SponsorEditComponent implements OnInit {
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

  sponsorId!: number;
  SponsorLevel = SponsorLevel;
  loading = false;
  loadingData = true;

  constructor(
    private sponsorService: SponsorService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.sponsorId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadSponsor();
  }

  loadSponsor() {
    this.loadingData = true;
    this.sponsorService.getSponsorById(this.sponsorId).subscribe({
      next: (sponsor: Sponsor) => {
        this.sponsor = {
          name: sponsor.name,
          description: sponsor.description,
          logo: sponsor.logo,
          website: sponsor.website,
          contactEmail: sponsor.contactEmail,
          contactPhone: sponsor.contactPhone,
          level: sponsor.level,
          contributionAmount: sponsor.contributionAmount
        };
        this.loadingData = false;
      },
      error: (err) => {
        console.error('Error loading sponsor:', err);
        this.notificationService.error('Error', 'Failed to load sponsor');
        this.router.navigate(['/dashboard/sponsors']);
      }
    });
  }

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

  updateSponsor() {
    console.log('updateSponsor() called');
    console.log('Sponsor data:', this.sponsor);
    
    if (!this.sponsor.name || this.sponsor.name.trim() === '') {
      this.notificationService.warning('Missing Fields', 'Name is required');
      return;
    }

    // Ensure contributionAmount is a proper number and round to 2 decimals
    const rawAmount = Number(this.sponsor.contributionAmount);
    this.sponsor.contributionAmount = Math.round(rawAmount * 100) / 100;
    
    console.log('Updating sponsor with contribution amount:', this.sponsor.contributionAmount, 'Type:', typeof this.sponsor.contributionAmount);
    console.log('Raw amount was:', rawAmount);
    console.log('Sponsor ID:', this.sponsorId);

    this.loading = true;
    this.sponsorService.updateSponsor(this.sponsorId, this.sponsor).subscribe({
      next: (response) => {
        console.log('Update successful:', response);
        this.notificationService.success('Success', 'Sponsor updated successfully');
        this.router.navigate(['/dashboard/sponsors']);
      },
      error: (err) => {
        console.error('Error updating sponsor:', err);
        console.error('Error details:', err.error);
        this.notificationService.error('Error', 'Failed to update sponsor');
        this.loading = false;
      }
    });
  }

  cancel() {
    this.router.navigate(['/dashboard/sponsors']);
  }
}
