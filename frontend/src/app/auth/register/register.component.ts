import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';
import { RecaptchaModule, RecaptchaFormsModule } from 'ng-recaptcha';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LogoComponent, RecaptchaModule, RecaptchaFormsModule],
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
  recaptchaToken: string | null = null;
  siteKey = '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'; // Google test key

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
      role: ['STUDENT', Validators.required], // Always STUDENT for public registration
      
      // Step 2: Personal Details
      phone: [''],
      cin: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      address: [''],
      city: [''],
      postalCode: [''],
      
      // Step 3: Profile & Experience
      bio: [''],
      englishLevel: ['', Validators.required], // Required for students
      yearsOfExperience: [null]
    });
  }

  selectRole(role: string): void {
    // Only STUDENT role is allowed for public registration
    this.registerForm.patchValue({ role: 'STUDENT' });
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
        return !!(this.englishLevel?.valid && this.recaptchaToken); // Require reCAPTCHA
      default:
        return false;
    }
  }

  onCaptchaResolved(token: string | null): void {
    this.recaptchaToken = token;
    console.log('reCAPTCHA resolved:', token);
  }

  onSubmit(): void {
    if (this.registerForm.invalid || !this.recaptchaToken) {
      this.errorMessage = 'Please complete the reCAPTCHA verification';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Add recaptcha token to form data
    const formData = {
      ...this.registerForm.value,
      recaptchaToken: this.recaptchaToken
    };

    this.authService.register(formData).subscribe({
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
