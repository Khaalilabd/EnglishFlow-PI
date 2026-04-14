import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { SponsorService } from '../../core/services/sponsor.service';
import { AuthService } from '../../core/services/auth.service';
import { CreateSponsorRequest, SponsorLevel } from '../../core/models/sponsor.model';

@Component({
  selector: 'app-apply-sponsor',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './apply-sponsor.component.html',
  styleUrls: ['./apply-sponsor.component.scss']
})
export class ApplySponsorComponent {

  currentStep = 1;
  totalSteps = 3;

  // Step 1 — Personal Info
  personal = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    cin: '',
    dateOfBirth: '',
    address: '',
    city: '',
    postalCode: ''
  };

  // Step 2 — Company / Sponsor Info
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

  // Step 3 — Account credentials
  account = {
    email: '',
    password: '',
    confirmPassword: ''
  };

  showPassword = false;
  showConfirmPassword = false;
  loading = false;
  success = false;
  error: string | null = null;
  SponsorLevel = SponsorLevel;

  constructor(
    private sponsorService: SponsorService,
    private authService: AuthService,
    private router: Router
  ) {}

  goHome(): void {
    this.router.navigate(['/']);
  }

  // ── Navigation ──────────────────────────────────────────────────────────────

  nextStep(): void {
    this.error = null;
    if (this.currentStep === 1 && !this.validateStep1()) return;
    if (this.currentStep === 2 && !this.validateStep2()) return;
    if (this.currentStep < this.totalSteps) this.currentStep++;
  }

  prevStep(): void {
    this.error = null;
    if (this.currentStep > 1) this.currentStep--;
  }

  // ── Validation ───────────────────────────────────────────────────────────────

  validateStep1(): boolean {
    const p = this.personal;
    if (!p.firstName || !p.lastName || !p.email || !p.phone ||
        !p.cin || !p.dateOfBirth || !p.address || !p.city ||
        !p.postalCode) {
      this.error = 'Please fill in all required fields.';
      return false;
    }
    if (!this.isValidEmail(p.email)) {
      this.error = 'Please enter a valid email address.';
      return false;
    }
    return true;
  }

  validateStep2(): boolean {
    if (!this.sponsor.name || !this.sponsor.contactEmail) {
      this.error = 'Company name and email are required.';
      return false;
    }
    if (!this.isValidEmail(this.sponsor.contactEmail!)) {
      this.error = 'Please enter a valid company email.';
      return false;
    }
    if (!this.sponsor.contributionAmount || Number(this.sponsor.contributionAmount) <= 0) {
      this.error = 'Please enter a valid contribution amount.';
      return false;
    }
    return true;
  }

  validateStep3(): boolean {
    if (!this.account.email || !this.account.password) {
      this.error = 'Email and password are required.';
      return false;
    }
    if (!this.isValidEmail(this.account.email)) {
      this.error = 'Please enter a valid email address.';
      return false;
    }
    if (this.account.password.length < 8) {
      this.error = 'Password must be at least 8 characters.';
      return false;
    }
    if (!/(?=.*[A-Z])(?=.*[a-z])(?=.*\d)/.test(this.account.password)) {
      this.error = 'Password must contain at least one uppercase letter, one lowercase letter, and one digit.';
      return false;
    }
    if (this.account.password !== this.account.confirmPassword) {
      this.error = 'Passwords do not match.';
      return false;
    }
    return true;
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  // ── Logo upload ──────────────────────────────────────────────────────────────

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      this.error = 'Please select an image file.';
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      this.error = 'Image size must be less than 5MB.';
      return;
    }
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.sponsor.logo = e.target?.result as string;
      this.error = null;
    };
    reader.readAsDataURL(file);
  }

  removeLogo(): void {
    this.sponsor.logo = '';
  }

  // ── Level helpers ────────────────────────────────────────────────────────────

  getAutoLevel(): string {
    const amount = Number(this.sponsor.contributionAmount) || 0;
    if (amount >= 1000) return '🥇 Gold';
    if (amount >= 500)  return '🥈 Silver';
    return '🥉 Bronze';
  }

  getAutoLevelValue(): SponsorLevel {
    const amount = Number(this.sponsor.contributionAmount) || 0;
    if (amount >= 1000) return SponsorLevel.GOLD;
    if (amount >= 500)  return SponsorLevel.SILVER;
    return SponsorLevel.BRONZE;
  }

  // ── Submit ───────────────────────────────────────────────────────────────────

  submit(): void {
    this.error = null;
    if (!this.validateStep3()) return;

    this.loading = true;

    // 1️⃣  Register the user account with role SPONSOR (dedicated endpoint, no reCAPTCHA)
    const registerRequest = {
      email:       this.account.email,
      password:    this.account.password,
      firstName:   this.personal.firstName,
      lastName:    this.personal.lastName,
      phone:       this.personal.phone,
      cin:         this.personal.cin,
      dateOfBirth: this.personal.dateOfBirth,
      address:     this.personal.address,
      city:        this.personal.city,
      postalCode:  this.personal.postalCode
    };

    this.authService.registerSponsor(registerRequest).pipe(
      switchMap((authResponse) => {
        // 2️⃣  Create the sponsor record linked to the new user
        const sponsorPayload: any = {
          ...this.sponsor,
          level:              this.getAutoLevelValue(),
          contributionAmount: Number(this.sponsor.contributionAmount) || 0,
          userId:             authResponse.id,
          applicantFirstName: this.personal.firstName,
          applicantLastName:  this.personal.lastName
        };
        return this.sponsorService.createSponsor(sponsorPayload);
      })
    ).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
      },
      error: (err: any) => {
        this.loading = false;
        
        // Check for CORS/OAuth2 redirect error
        if (err.status === 0 && err.url?.includes('oauth2/authorization')) {
          this.error = 'Configuration error: The registration endpoint is incorrectly configured. Please contact the administrator.';
          console.error('CORS/OAuth2 redirect error - Backend security configuration needs to be fixed');
          return;
        }
        
        const msg: string = err?.error?.message ?? '';
        const detail: string = JSON.stringify(err?.error ?? '').toLowerCase();
        if (msg.toLowerCase().includes('email already exists') || err?.status === 409) {
          this.error = 'This email is already registered. Please use a different email.';
        } else if (detail.includes('cin') || detail.includes('duplicate') || detail.includes('unique')) {
          this.error = 'This CIN is already registered. Please check your information.';
        } else {
          this.error = msg || 'Failed to submit application. Please try again.';
        }
        console.error('Sponsor application error:', err);
      }
    });
  }

  // ── Reset ────────────────────────────────────────────────────────────────────

  resetForm(): void {
    this.success     = false;
    this.currentStep = 1;
    this.error       = null;
    this.personal    = { firstName: '', lastName: '', email: '', phone: '', cin: '',
                         dateOfBirth: '', address: '', city: '', postalCode: '' };
    this.sponsor     = { name: '', description: '', logo: '', website: '',
                         contactEmail: '', contactPhone: '', level: SponsorLevel.BRONZE, contributionAmount: 0 };
    this.account     = { email: '', password: '', confirmPassword: '' };
  }
}
