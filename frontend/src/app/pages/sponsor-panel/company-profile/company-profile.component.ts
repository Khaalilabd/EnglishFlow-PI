import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Sponsor } from '../../../core/models/sponsor.model';

@Component({
  selector: 'app-company-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './company-profile.component.html'
})
export class CompanyProfileComponent implements OnInit {
  loading = true;
  saving = false;
  currentUser: any;
  sponsor: Sponsor | null = null;

  form = {
    name: '',
    description: '',
    logo: '',
    website: '',
    contactEmail: '',
    contactPhone: ''
  };

  isDragging = false;

  constructor(
    private authService: AuthService,
    private sponsorService: SponsorService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.loadSponsor();
  }

  loadSponsor() {
    const userId = this.currentUser?.id;
    if (!userId) { this.loading = false; return; }

    this.sponsorService.getSponsorsByUser(userId).subscribe({
      next: (sponsors) => {
        // Platform sponsorship (no clubId)
        this.sponsor = sponsors.find(s => s.status === 'APPROVED' && !s.clubId) || null;
        if (this.sponsor) {
          this.form = {
            name: this.sponsor.name || '',
            description: this.sponsor.description || '',
            logo: this.sponsor.logo || '',
            website: this.sponsor.website || '',
            contactEmail: this.sponsor.contactEmail || '',
            contactPhone: this.sponsor.contactPhone || ''
          };
        }
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) this.handleFile(file);
  }

  onDragOver(e: DragEvent) { e.preventDefault(); this.isDragging = true; }
  onDragLeave(e: DragEvent) { e.preventDefault(); this.isDragging = false; }
  onDrop(e: DragEvent) {
    e.preventDefault();
    this.isDragging = false;
    const file = e.dataTransfer?.files[0];
    if (file) this.handleFile(file);
  }

  handleFile(file: File) {
    if (!file.type.startsWith('image/')) {
      this.notificationService.warning('Invalid File', 'Please select an image file');
      return;
    }
    if (file.size > 2 * 1024 * 1024) {
      this.notificationService.warning('File Too Large', 'Logo must be less than 2MB');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e: any) => { this.form.logo = e.target.result; };
    reader.readAsDataURL(file);
  }

  removeLogo() { this.form.logo = ''; }

  save() {
    if (!this.sponsor?.id) return;
    this.saving = true;
    this.sponsorService.updateSponsor(this.sponsor.id, {
      name: this.form.name,
      description: this.form.description,
      logo: this.form.logo,
      website: this.form.website,
      contactEmail: this.form.contactEmail,
      contactPhone: this.form.contactPhone,
      contributionAmount: this.sponsor.contributionAmount
    }).subscribe({
      next: () => {
        this.saving = false;
        this.notificationService.success('Saved', 'Company profile updated successfully');
        this.loadSponsor();
      },
      error: () => {
        this.saving = false;
        this.notificationService.error('Error', 'Failed to save changes');
      }
    });
  }
}
