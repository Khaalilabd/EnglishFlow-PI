import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LogoComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';
  currentStep = 1;
  totalSteps = 3;
  profilePhotoPreview: string | null = null;

  englishLevels = ['Beginner', 'Elementary', 'Intermediate', 'Upper Intermediate', 'Advanced', 'Proficient'];
  experienceYears = Array.from({length: 31}, (_, i) => i); // 0-30 years

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      // Step 1: Basic Info
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      role: ['STUDENT', Validators.required],
      
      // Step 2: Personal Details
      phone: [''],
      cin: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      address: [''],
      city: [''],
      postalCode: [''],
      
      // Step 3: Profile & Experience
      bio: [''],
      englishLevel: [''],
      yearsOfExperience: [null]
    });
  }

  selectRole(role: string): void {
    this.registerForm.patchValue({ role });
    
    // Update validators based on role
    if (role === 'STUDENT') {
      this.registerForm.get('englishLevel')?.setValidators([Validators.required]);
      this.registerForm.get('yearsOfExperience')?.clearValidators();
    } else {
      this.registerForm.get('yearsOfExperience')?.setValidators([Validators.required]);
      this.registerForm.get('englishLevel')?.clearValidators();
    }
    
    this.registerForm.get('englishLevel')?.updateValueAndValidity();
    this.registerForm.get('yearsOfExperience')?.updateValueAndValidity();
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePhotoPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  nextStep(): void {
    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  isStepValid(step: number): boolean {
    switch(step) {
      case 1:
        return !!(this.email?.valid && this.password?.valid && 
                 this.firstName?.valid && this.lastName?.valid);
      case 2:
        return !!(this.cin?.valid && this.dateOfBirth?.valid);
      case 3:
        if (this.role?.value === 'STUDENT') {
          return !!this.englishLevel?.valid;
        } else {
          return !!this.yearsOfExperience?.valid;
        }
      default:
        return false;
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Registration error:', error);
        this.errorMessage = error.error?.message || 'An error occurred during registration';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get firstName() { return this.registerForm.get('firstName'); }
  get lastName() { return this.registerForm.get('lastName'); }
  get role() { return this.registerForm.get('role'); }
  get cin() { return this.registerForm.get('cin'); }
  get dateOfBirth() { return this.registerForm.get('dateOfBirth'); }
  get englishLevel() { return this.registerForm.get('englishLevel'); }
  get yearsOfExperience() { return this.registerForm.get('yearsOfExperience'); }
}
