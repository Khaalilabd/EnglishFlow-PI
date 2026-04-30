import { Component, OnInit, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/user.model';
import { environment } from '../../../../environments/environment';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-admin-settings',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
  schemas: [NO_ERRORS_SCHEMA]
})
export class AdminSettingsComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  activeTab: 'profile' | 'security' | 'notifications' | 'appearance' = 'profile';
  
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  notificationForm!: FormGroup;
  
  isLoadingProfile = false;
  isLoadingPassword = false;
  isLoadingNotifications = false;
  isLoadingSessions = false;
  
  profilePhotoPreview: string | null = null;
  selectedFile: File | null = null;
  
  profileCompletion = 0;
  twoFactorStatus: any = null;
  showSetupModal = false;
  showDisableModal = false;
  showBackupCodesModal = false;
  showSessionsModal = false;
  activeSessions: any[] = [];
  isLoading2FA = false;
  backupCodes: string[] = [];
  verificationCode = '';
  setupData: any = null;
  darkMode = false;
  isDarkMode = false;
  sessionSummary: any = null;
  passwordStrength = 'weak';
  isDragging = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly http: HttpClient
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    
    // Load fresh user data from backend
    if (this.currentUser) {
      this.loadUserProfile();
    }
    
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.initializeForms();
      }
    });
    
    this.initializeForms();
  }

  loadUserProfile() {
    if (!this.currentUser) return;
    
    this.http.get<any>(`${environment.apiUrl}/users/${this.currentUser.id}`).subscribe({
      next: (userData) => {
        // Update current user with fresh data
        if (this.currentUser) {
          const updatedUser: AuthResponse = {
            ...this.currentUser,
            ...userData
          };
          this.currentUser = updatedUser;
          
          // Update in authService to persist in localStorage
          this.authService.updateCurrentUser(updatedUser);
          
          this.initializeForms();
        }
      },
      error: (error) => {
        console.error('Failed to load user profile:', error);
      }
    });
  }

  initializeForms() {
    this.profileForm = this.fb.group({
      firstName: [this.currentUser?.firstName || '', [Validators.required, Validators.minLength(2)]],
      lastName: [this.currentUser?.lastName || '', [Validators.required, Validators.minLength(2)]],
      email: [{ value: this.currentUser?.email || '', disabled: true }],
      phone: [this.currentUser?.phone || ''],
      dateOfBirth: [this.currentUser?.dateOfBirth || ''],
      address: [this.currentUser?.address || ''],
      city: [this.currentUser?.city || ''],
      postalCode: [this.currentUser?.postalCode || ''],
      bio: [this.currentUser?.bio || '', [Validators.maxLength(500)]]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required, Validators.minLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });

    this.notificationForm = this.fb.group({
      emailNotifications: [true],
      pushNotifications: [true],
      complaintUpdates: [true],
      messageNotifications: [true],
      weeklyDigest: [false]
    });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value ? null : { 'mismatch': true };
  }

  setActiveTab(tab: 'profile' | 'security' | 'notifications' | 'appearance') {
    this.activeTab = tab;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePhotoPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  uploadProfilePhoto() {
    if (!this.selectedFile || !this.currentUser) return;
    const formData = new FormData();
    formData.append('file', this.selectedFile);
    
    // Use correct endpoint
    this.http.post<any>(`${environment.apiUrl}/users/${this.currentUser.id}/upload-photo`, formData).subscribe({
      next: (response) => {
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Profile photo updated', confirmButtonColor: '#3B82F6', timer: 2000 });
        this.profilePhotoPreview = null;
        this.selectedFile = null;
        
        // Update current user with new photo and refresh from backend
        if (this.currentUser) {
          const updatedUser: AuthResponse = {
            ...this.currentUser,
            profilePhoto: response.profilePhoto
          };
          this.authService.updateCurrentUser(updatedUser);
          
          // Reload user profile to get fresh data
          this.loadUserProfile();
        }
        
        window.dispatchEvent(new CustomEvent('profilePhotoUpdated', { detail: { profilePhoto: response.profilePhoto } }));
      },
      error: (error) => {
        console.error('Upload error:', error);
        Swal.fire({ icon: 'error', title: 'Error!', text: error.error?.error || 'Failed to upload photo', confirmButtonColor: '#3B82F6' });
      }
    });
  }

  onSubmitProfile() {
    if (this.profileForm.invalid) {
      Object.keys(this.profileForm.controls).forEach(key => this.profileForm.get(key)?.markAsTouched());
      return;
    }
    this.isLoadingProfile = true;
    this.authService.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: () => {
        this.isLoadingProfile = false;
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Profile updated', confirmButtonColor: '#3B82F6', timer: 2000 });
      },
      error: (error) => {
        this.isLoadingProfile = false;
        Swal.fire({ icon: 'error', title: 'Error!', text: error.error?.message || 'Failed to update', confirmButtonColor: '#3B82F6' });
      }
    });
  }

  onSubmitPassword() {
    if (this.passwordForm.invalid) {
      Object.keys(this.passwordForm.controls).forEach(key => this.passwordForm.get(key)?.markAsTouched());
      return;
    }
    this.isLoadingPassword = true;
    const { currentPassword, newPassword } = this.passwordForm.value;
    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.isLoadingPassword = false;
        this.passwordForm.reset();
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Password changed', confirmButtonColor: '#3B82F6', timer: 2000 });
      },
      error: (error) => {
        this.isLoadingPassword = false;
        Swal.fire({ icon: 'error', title: 'Error!', text: error.error?.message || 'Failed to change password', confirmButtonColor: '#3B82F6' });
      }
    });
  }

  onSubmitNotifications() {
    this.isLoadingNotifications = true;
    setTimeout(() => {
      this.isLoadingNotifications = false;
      Swal.fire({ icon: 'success', title: 'Success!', text: 'Preferences updated', confirmButtonColor: '#3B82F6', timer: 2000 });
    }, 1000);
  }

  getProfilePhotoUrl(): string {
    if (this.profilePhotoPreview) return this.profilePhotoPreview;
    if (this.currentUser?.profilePhoto) {
      if (this.currentUser.profilePhoto.startsWith('http')) return this.currentUser.profilePhoto;
      return `http://localhost:8081${this.currentUser.profilePhoto}`;
    }
    const name = `${this.currentUser?.firstName || 'User'}+${this.currentUser?.lastName || 'Name'}`;
    return `https://ui-avatars.com/api/?name=${name}&background=3B82F6&color=fff&size=256`;
  }

  getFieldError(formGroup: FormGroup, fieldName: string): string {
    const field = formGroup.get(fieldName);
    if (field?.hasError('required')) return 'This field is required';
    if (field?.hasError('minlength')) return `Minimum ${field.errors?.['minlength'].requiredLength} characters`;
    if (field?.hasError('maxlength')) return `Maximum ${field.errors?.['maxlength'].requiredLength} characters`;
    if (field?.hasError('pattern')) return 'Invalid format';
    if (field?.hasError('email')) return 'Invalid email address';
    return '';
  }

  revokeAllOtherSessions() {
    // Placeholder implementation
  }

  closeSessionsModal() {
    this.showSessionsModal = false;
  }

  revokeSession(sessionId: string, sessionName: string) {
    // Placeholder implementation
  }

  formatLocation(session: any): string {
    return session.location || 'Unknown';
  }

  closeBackupCodesModal() {
    this.showBackupCodesModal = false;
  }

  getDeviceIcon(deviceType: string): string {
    return 'fa-laptop';
  }

  getBrowserIcon(browserName: string): string {
    return 'fa-chrome';
  }

  getOSIcon(operatingSystem: string): string {
    return 'fa-windows';
  }

  downloadBackupCodes() {
    // Placeholder implementation
  }

  setup2FA() {
    // Placeholder implementation
  }

  disable2FA() {
    // Placeholder implementation
  }

  openSetupModal() {
    this.showSetupModal = true;
  }

  openDisableModal() {
    this.showDisableModal = true;
  }

  closeSetupModal() {
    this.showSetupModal = false;
  }

  closeDisableModal() {
    this.showDisableModal = false;
  }

  openBackupCodesModal() {
    this.showBackupCodesModal = true;
  }

  openSessionsModal() {
    this.showSessionsModal = true;
  }

  enable2FA() {
    // Placeholder implementation
  }

  toggleDarkMode() {
    this.darkMode = !this.darkMode;
  }

  openSetup2FA() {
    this.showSetupModal = true;
  }

  openDisable2FA() {
    this.showDisableModal = true;
  }

  regenerateBackupCodes() {
    // Placeholder implementation
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const file = files[0];
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePhotoPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }
}
