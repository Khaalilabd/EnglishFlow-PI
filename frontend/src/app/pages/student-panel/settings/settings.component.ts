import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthResponse } from '../../../core/models/user.model';
import { HttpClient } from '@angular/common/http';

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
  isUploadingImage = false;
  
  profilePicturePreview: string | null = null;
  selectedFile: File | null = null;
  
  // Notification settings
  emailNotifications = true;
  pushNotifications = true;
  courseUpdates = true;
  assignmentReminders = true;

  englishLevels = [
    { value: 'A1', label: 'A1 - Beginner' },
    { value: 'A2', label: 'A2 - Elementary' },
    { value: 'B1', label: 'B1 - Intermediate' },
    { value: 'B2', label: 'B2 - Upper Intermediate' },
    { value: 'C1', label: 'C1 - Advanced' },
    { value: 'C2', label: 'C2 - Proficient' }
  ];

  private apiUrl = 'http://localhost:8080/api/users'; // Via API Gateway

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private http: HttpClient
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
      cin: [''],
      dateOfBirth: [''],
      address: [''],
      city: [''],
      postalCode: [''],
      bio: ['', [Validators.maxLength(500)]],
      englishLevel: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  loadUserData() {
    if (this.currentUser) {
      // Charger les données complètes de l'utilisateur depuis l'API
      this.http.get<any>(`${this.apiUrl}/${this.currentUser.id}`).subscribe({
        next: (userData) => {
          console.log('User data loaded:', userData);
          this.profileForm.patchValue({
            firstName: userData.firstName || '',
            lastName: userData.lastName || '',
            email: userData.email || '',
            phone: userData.phone || '',
            cin: userData.cin || '',
            dateOfBirth: userData.dateOfBirth || '',
            address: userData.address || '',
            city: userData.city || '',
            postalCode: userData.postalCode || '',
            bio: userData.bio || '',
            englishLevel: userData.englishLevel || ''
          });
          
          // Construire l'URL complète de la photo
          if (userData.profilePhoto) {
            this.profilePicturePreview = `http://localhost:8081${userData.profilePhoto}`;
            console.log('Profile photo URL:', this.profilePicturePreview);
          } else {
            this.profilePicturePreview = this.getDefaultAvatar();
          }
        },
        error: (error) => {
          console.error('Error loading user data:', error);
          // Fallback to currentUser data
          this.profileForm.patchValue({
            firstName: this.currentUser?.firstName || '',
            lastName: this.currentUser?.lastName || '',
            email: this.currentUser?.email || '',
            phone: this.currentUser?.phone || ''
          });
          this.profilePicturePreview = this.currentUser?.profilePhoto 
            ? `http://localhost:8081${this.currentUser.profilePhoto}`
            : this.getDefaultAvatar();
        }
      });
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
      console.log('File selected:', file.name, file.type, file.size);
      
      // Liste des formats d'image acceptés
      const acceptedFormats = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
      const fileExtension = file.name.split('.').pop()?.toLowerCase();
      
      // Vérifier le type MIME et l'extension
      if (!acceptedFormats.includes(file.type) || ['heic', 'heif'].includes(fileExtension || '')) {
        console.error('Invalid format:', file.type, fileExtension);
        this.toastService.error('Unsupported format. Please use JPG, PNG, GIF or WebP');
        event.target.value = ''; // Reset input
        return;
      }

      // Vérifier la taille (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        console.error('File too large:', file.size);
        this.toastService.error('Image size must be less than 5MB');
        event.target.value = ''; // Reset input
        return;
      }

      this.selectedFile = file;
      
      // Preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePicturePreview = e.target.result;
        console.log('Preview loaded');
      };
      reader.onerror = (error) => {
        console.error('FileReader error:', error);
        this.toastService.error('Failed to read file');
      };
      reader.readAsDataURL(file);

      // Upload immédiatement
      this.uploadProfilePicture();
    }
  }

  uploadProfilePicture() {
    if (!this.selectedFile || !this.currentUser) {
      console.error('No file or user:', this.selectedFile, this.currentUser);
      return;
    }

    console.log('Starting upload for user:', this.currentUser.id);
    this.isUploadingImage = true;
    const formData = new FormData();
    formData.append('file', this.selectedFile);

    // Upload vers l'API
    this.http.post<any>(`${this.apiUrl}/${this.currentUser.id}/upload-photo`, formData).subscribe({
      next: (response) => {
        console.log('Upload success:', response);
        this.isUploadingImage = false;
        this.toastService.success('Profile picture updated successfully!');
        
        // Construire l'URL complète
        const fullPhotoUrl = `http://localhost:8081${response.profilePhoto}`;
        
        // Mettre à jour la photo dans le localStorage et le service
        if (this.currentUser) {
          const updated = {
            ...this.currentUser,
            profilePhoto: response.profilePhoto
          };
          localStorage.setItem('currentUser', JSON.stringify(updated));
          this.authService.updateCurrentUser(updated);
          this.profilePicturePreview = fullPhotoUrl;
          
          // Émettre un événement pour mettre à jour la photo dans le sidebar
          window.dispatchEvent(new CustomEvent('profilePhotoUpdated', { 
            detail: { profilePhoto: fullPhotoUrl } 
          }));
        }
      },
      error: (error) => {
        console.error('Upload error:', error);
        this.isUploadingImage = false;
        
        let errorMessage = 'Failed to upload image. Please try again.';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please check if the backend is running.';
        } else if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.status === 413) {
          errorMessage = 'Image is too large. Maximum size is 5MB.';
        } else if (error.status === 415) {
          errorMessage = 'Unsupported image format.';
        }
        
        this.toastService.error(errorMessage);
        // Restaurer l'ancienne photo
        this.profilePicturePreview = this.currentUser?.profilePhoto 
          ? `http://localhost:8081${this.currentUser.profilePhoto}`
          : this.getDefaultAvatar();
        this.selectedFile = null;
      }
    });
  }

  removeProfilePicture() {
    if (!this.currentUser) return;

    this.http.delete(`${this.apiUrl}/${this.currentUser.id}/photo`).subscribe({
      next: () => {
        this.profilePicturePreview = this.getDefaultAvatar();
        this.selectedFile = null;
        this.toastService.success('Profile picture removed successfully');
        
        // Mettre à jour dans le localStorage
        if (this.currentUser) {
          const updated = {
            ...this.currentUser,
            profilePhoto: null
          };
          localStorage.setItem('currentUser', JSON.stringify(updated));
          this.authService.updateCurrentUser(updated);
          
          // Émettre un événement
          window.dispatchEvent(new CustomEvent('profilePhotoUpdated', { 
            detail: { profilePhoto: null } 
          }));
        }
      },
      error: (error) => {
        console.error('Error removing photo:', error);
        this.toastService.error('Failed to remove photo. Please try again.');
      }
    });
  }

  toggleEditProfile() {
    this.isEditingProfile = !this.isEditingProfile;
    if (!this.isEditingProfile) {
      this.loadUserData();
    }
  }

  saveProfile() {
    if (this.profileForm.valid && this.currentUser) {
      this.isSavingProfile = true;
      
      const updateData = this.profileForm.value;
      
      // Utiliser l'endpoint approprié selon le rôle
      // ADMIN et ACADEMIC_OFFICE_AFFAIR utilisent l'endpoint admin
      // STUDENT et TUTOR utilisent l'endpoint users
      const endpoint = (this.currentUser.role === 'ADMIN' || this.currentUser.role === 'ACADEMIC_OFFICE_AFFAIR')
        ? `http://localhost:8080/api/admin/users/${this.currentUser.id}`
        : `${this.apiUrl}/${this.currentUser.id}`;
      
      // Appel API pour mettre à jour le profil
      this.http.put<any>(endpoint, updateData).subscribe({
        next: (updatedUser) => {
          this.isSavingProfile = false;
          this.isEditingProfile = false;
          
          // Mettre à jour le currentUser
          const updated = {
            ...this.currentUser!,
            ...updatedUser
          };
          localStorage.setItem('currentUser', JSON.stringify(updated));
          this.authService.updateCurrentUser(updated);
          this.currentUser = updated;
          
          this.toastService.success('Profile updated successfully!');
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.isSavingProfile = false;
          this.toastService.error(error.error?.message || 'Failed to update profile. Please try again.');
        }
      });
    }
  }

  changePassword() {
    if (this.passwordForm.valid) {
      this.isChangingPassword = true;
      
      // TODO: Implement API call to change password
      setTimeout(() => {
        this.isChangingPassword = false;
        this.passwordForm.reset();
        this.toastService.success('Password changed successfully!');
      }, 1000);
    }
  }

  setActiveTab(tab: 'profile' | 'security' | 'notifications') {
    this.activeTab = tab;
  }

  get bioLength(): number {
    return this.profileForm.get('bio')?.value?.length || 0;
  }
}
