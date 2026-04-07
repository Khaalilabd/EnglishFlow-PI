import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { ClubCategory, Skill } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './club-create.component.html',
  styleUrls: ['./club-create.component.scss']
})
export class ClubCreateComponent {
  clubForm: FormGroup;
  loading = false;
  error: string | null = null;
  categories = Object.values(ClubCategory);
  currentUserId: number | null = null;
  
  // Wizard
  currentStep: number = 1;
  totalSteps: number = 2;
  
  // Skills management
  clubSkills: Skill[] = [];
  newSkillName: string = '';
  newSkillDescription: string = '';
  
  // Image upload
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService,
    private authService: AuthService,
    private router: Router
  ) {
    this.clubForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      objective: [''],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]],
      registrationFee: [0, [Validators.min(0)]],
      image: ['']
    });
    
    // Get current user ID
    const user = this.authService.currentUserValue;
    if (user && user.id !== undefined && user.id !== null) {
      this.currentUserId = user.id;
    }
  }

  // Wizard navigation
  nextStep() {
    if (this.currentStep === 1) {
      // Valider les champs de la page 1
      const step1Fields = ['name', 'description', 'category', 'maxMembers'];
      let isValid = true;
      
      step1Fields.forEach(field => {
        const control = this.clubForm.get(field);
        if (control) {
          control.markAsTouched();
          if (control.invalid) {
            isValid = false;
          }
        }
      });
      
      if (isValid) {
        this.currentStep = 2;
      }
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  // Skills management
  addSkill() {
    if (this.newSkillName.trim()) {
      this.clubSkills.push({
        name: this.newSkillName.trim(),
        description: this.newSkillDescription.trim() || undefined
      });
      this.newSkillName = '';
      this.newSkillDescription = '';
    }
  }

  removeSkill(index: number) {
    this.clubSkills.splice(index, 1);
  }

  // Image upload
  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      if (!file.type.startsWith('image/')) {
        this.error = 'Please select an image file';
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        this.error = 'Image size must be less than 5MB';
        return;
      }
      
      this.selectedImageFile = file;
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  private async convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }

  async onSubmit() {
    if (this.clubForm.invalid) {
      this.clubForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    try {
      const clubData: any = { ...this.clubForm.value };
      
      // Ajouter le createdBy (ID de l'utilisateur courant)
      if (this.currentUserId) {
        clubData.createdBy = this.currentUserId;
      }
      
      // Ajouter les skills
      if (this.clubSkills.length > 0) {
        clubData.skills = this.clubSkills;
      }
      
      // Ajouter l'image
      if (this.selectedImageFile) {
        clubData.image = await this.convertFileToBase64(this.selectedImageFile);
      }

      this.clubService.createClub(clubData).subscribe({
        next: (club) => {
          this.router.navigate(['/user-panel/clubs']);
        },
        error: (err) => {
          console.error('Error creating club:', err);
          this.error = err.error?.message || 'Failed to create club. Please try again.';
          this.loading = false;
        }
      });
    } catch (error) {
      this.error = 'Failed to process image. Please try again.';
      this.loading = false;
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
