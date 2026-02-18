import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
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
  
  profilePicturePreview: string | null = null;
  selectedFile: File | null = null;
  
  // Notification settings
  emailNotifications = true;
  pushNotifications = true;
  courseUpdates = true;
  assignmentReminders = true;

  private apiUrl = 'http://localhost:8080/api';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
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
      yearsOfExperience: ['']
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
      const endpoint = (this.currentUser.role === 'ADMIN' || this.currentUser.role === 'ACADEMIC_OFFICE_AFFAIR')
        ? `${this.apiUrl}/admin/users/${this.currentUser.id}`
        : `${this.apiUrl}/users/${this.currentUser.id}`;
      
      this.http.get<any>(endpoint).subscribe({
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
            yearsOfExperience: userData.yearsOfExperience || ''
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
    if (this.profileForm.valid && this.currentUser) {
      this.isSavingProfile = true;
      
      const updateData = this.profileForm.value;
      
      // Utiliser l'endpoint admin pour ADMIN et ACADEMIC_OFFICE_AFFAIR
      const endpoint = (this.currentUser.role === 'ADMIN' || this.currentUser.role === 'ACADEMIC_OFFICE_AFFAIR')
        ? `${this.apiUrl}/admin/users/${this.currentUser.id}`
        : `${this.apiUrl}/users/${this.currentUser.id}`;
      
      // Appel API pour mettre à jour le profil
      this.http.put<any>(endpoint, updateData).subscribe({
        next: (updatedUser) => {
          console.log('Profile updated:', updatedUser);
          this.isSavingProfile = false;
          this.isEditingProfile = false;
          
          // Mettre à jour le currentUser dans le service et localStorage
          const updated = {
            ...this.currentUser!,
            firstName: updatedUser.firstName,
            lastName: updatedUser.lastName,
            email: updatedUser.email,
            phone: updatedUser.phone,
            profilePhoto: updatedUser.profilePhoto
          };
          
          // Mettre à jour dans le localStorage
          localStorage.setItem('currentUser', JSON.stringify(updated));
          this.authService.updateCurrentUser(updated);
          
          // Recharger les données
          this.currentUser = updated;
          this.profilePicturePreview = updated.profilePhoto || this.getDefaultAvatar();
          
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

  get bioLength(): number {
    return this.profileForm.get('bio')?.value?.length || 0;
  }
}
