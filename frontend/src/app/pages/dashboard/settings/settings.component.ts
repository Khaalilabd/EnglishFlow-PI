import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  
  activeTab: 'profile' | 'security' | 'notifications' = 'profile';
  
  isEditingProfile = false;
  isSavingProfile = false;
  isChangingPassword = false;
  
  profilePicturePreview: string | null = null;
  selectedFile: File | null = null;
  
  // Notification settings
  emailNotifications = true;
  pushNotifications = true;
  courseUpdates = true;
  assignmentReminders = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.initializeForms();
    this.loadUserData();
  }

  initializeForms() {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.pattern(/^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{1,9}$/)]],
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  loadUserData() {
    if (this.currentUser) {
      this.profileForm.patchValue({
        firstName: this.currentUser.firstName,
        lastName: this.currentUser.lastName,
        email: this.currentUser.email,
        phone: this.currentUser.phone || ''
      });
      
      this.profilePicturePreview = this.currentUser.profilePhoto || this.getDefaultAvatar();
    }
  }

  getDefaultAvatar(): string {
    const name = `${this.currentUser?.firstName || 'User'}+${this.currentUser?.lastName || 'Name'}`;
    return `https://ui-avatars.com/api/?name=${name}&background=F6BD60&color=fff&size=256`;
  }

  passwordMatchValidator(group: FormGroup) {
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      
      // Preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePicturePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeProfilePicture() {
    this.profilePicturePreview = this.getDefaultAvatar();
    this.selectedFile = null;
  }

  toggleEditProfile() {
    this.isEditingProfile = !this.isEditingProfile;
    if (!this.isEditingProfile) {
      this.loadUserData();
    }
  }

  saveProfile() {
    if (this.profileForm.valid) {
      this.isSavingProfile = true;
      
      const updateData: any = {
        ...this.profileForm.value
      };
      
      // Ajouter la photo si elle a été modifiée
      if (this.selectedFile) {
        updateData.profilePhoto = this.profilePicturePreview;
      }
      
      // Appel API pour mettre à jour le profil
      this.authService.updateProfile(updateData).subscribe({
        next: (updatedUser) => {
          console.log('Profile updated:', updatedUser);
          this.isSavingProfile = false;
          this.isEditingProfile = false;
          
          // Mettre à jour le currentUser dans le service et localStorage
          const currentUser = this.authService.currentUserValue;
          if (currentUser) {
            const updated = {
              ...currentUser,
              firstName: updatedUser.firstName,
              lastName: updatedUser.lastName,
              email: updatedUser.email,
              phone: updatedUser.phone,
              profilePhoto: updatedUser.profilePhoto
            };
            
            // Mettre à jour dans le localStorage
            localStorage.setItem('currentUser', JSON.stringify(updated));
            
            // Recharger les données
            this.currentUser = updated;
            this.profilePicturePreview = updated.profilePhoto || this.getDefaultAvatar();
          }
          
          alert('Profile updated successfully!');
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.isSavingProfile = false;
          alert('Failed to update profile. Please try again.');
        }
      });
    }
  }

  changePassword() {
    if (this.passwordForm.valid) {
      this.isChangingPassword = true;
      
      // TODO: Implement API call to change password
      setTimeout(() => {
        console.log('Password changed');
        this.isChangingPassword = false;
        this.passwordForm.reset();
      }, 1000);
    }
  }

  setActiveTab(tab: 'profile' | 'security' | 'notifications') {
    this.activeTab = tab;
  }
}
