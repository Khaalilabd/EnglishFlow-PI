import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { SkillService } from '../../../core/services/skill.service';
import { ClubCategory, Skill } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './club-edit.component.html',
  styleUrls: ['./club-edit.component.scss']
})
export class ClubEditComponent implements OnInit {
  clubForm: FormGroup;
  clubId!: number;
  loading = false;
  saving = false;
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
  currentImage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private authService: AuthService,
    private skillService: SkillService
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
  }

  ngOnInit() {
    this.getCurrentUser();
    this.route.params.subscribe(params => {
      this.clubId = +params['id'];
      this.loadClub();
      this.loadSkills();
    });
  }

  getCurrentUser() {
    const user = this.authService.currentUserValue;
    if (user && user.id !== undefined && user.id !== null) {
      this.currentUserId = user.id;
    } else {
      console.error('No user found or user has no ID');
      this.error = 'User not authenticated. Please log in again.';
    }
  }

  loadClub() {
    this.loading = true;
    this.error = null;

    this.clubService.getClubById(this.clubId).subscribe({
      next: (club) => {
        this.clubForm.patchValue({
          name: club.name,
          description: club.description,
          objective: club.objective,
          category: club.category,
          maxMembers: club.maxMembers,
          registrationFee: club.registrationFee || 0
        });
        this.currentImage = club.image || null;
        this.imagePreview = club.image || null;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading club:', err);
        this.error = 'Failed to load club details.';
        this.loading = false;
      }
    });
  }

  loadSkills() {
    this.skillService.getSkillsByClub(this.clubId).subscribe({
      next: (skills) => {
        this.clubSkills = skills;
      },
      error: (err) => {
        console.error('Error loading skills:', err);
      }
    });
  }

  // Wizard navigation
  nextStep() {
    if (this.currentStep === 1) {
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

    if (!this.currentUserId) {
      this.error = 'User not authenticated. Please log in again.';
      return;
    }

    this.saving = true;
    this.error = null;

    try {
      const clubData: any = { ...this.clubForm.value };
      
      // Ajouter les skills
      if (this.clubSkills.length > 0) {
        clubData.skills = this.clubSkills;
      }
      
      // Ajouter l'image si changée
      if (this.selectedImageFile) {
        clubData.image = await this.convertFileToBase64(this.selectedImageFile);
      } else if (this.currentImage) {
        clubData.image = this.currentImage;
      }

      this.clubService.updateClub(this.clubId, clubData, this.currentUserId).subscribe({
        next: () => {
          alert('Demande de modification créée avec succès ! Elle doit être approuvée par le vice-président et le secrétaire.');
          this.router.navigate(['/user-panel/clubs']);
        },
        error: (err) => {
          console.error('Error creating update request:', err);
          if (err.error && err.error.message) {
            this.error = err.error.message;
          } else if (err.error && typeof err.error === 'string') {
            this.error = err.error;
          } else {
            this.error = 'Failed to create update request. Please try again.';
          }
          this.saving = false;
        }
      });
    } catch (error) {
      this.error = 'Failed to process image. Please try again.';
      this.saving = false;
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  cancel() {
    // Retourner à la page de détails du club
    this.router.navigate(['/user-panel/clubs', this.clubId], { replaceUrl: true });
  }
}
