import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sponsor-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SponsorSettingsComponent implements OnInit {
  currentUser: any = null;
  loading = false;
  successMessage = '';
  errorMessage = '';

  // Profile settings
  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  companyName = '';
  companyWebsite = '';

  // Password change
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  constructor(
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    this.currentUser = this.authService.currentUserValue;
    if (this.currentUser) {
      this.firstName = this.currentUser.firstName || '';
      this.lastName = this.currentUser.lastName || '';
      this.email = this.currentUser.email || '';
      this.phone = this.currentUser.phone || '';
      this.companyName = this.currentUser.companyName || '';
      this.companyWebsite = this.currentUser.companyWebsite || '';
    }
  }

  updateProfile() {
    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const profileData = {
      firstName: this.firstName,
      lastName: this.lastName,
      phone: this.phone,
      companyName: this.companyName,
      companyWebsite: this.companyWebsite
    };

    this.authService.updateProfile(profileData).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.successMessage = 'Profile updated successfully!';
        // Update local storage
        const updatedUser = { ...this.currentUser, ...profileData };
        localStorage.setItem('currentUser', JSON.stringify(updatedUser));
        this.currentUser = updatedUser;
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = 'Failed to update profile. Please try again.';
        console.error('Profile update error:', error);
      }
    });
  }

  changePassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New passwords do not match!';
      return;
    }

    if (this.newPassword.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters long!';
      return;
    }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.authService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Password changed successfully!';
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Failed to change password. Please check your current password.';
        console.error('Password change error:', error);
      }
    });
  }
}
