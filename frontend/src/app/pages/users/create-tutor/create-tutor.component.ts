import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { PasswordModalComponent } from './password-modal/password-modal.component';

@Component({
  selector: 'app-create-tutor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PasswordModalComponent],
  templateUrl: './create-tutor.component.html',
  styleUrls: ['./create-tutor.component.scss']
})
export class CreateTutorComponent {
  tutorForm: FormGroup;
  currentStep = 1;
  totalSteps = 3;
  loading = false;
  errorMessage = '';
  showPasswordModal = false;
  generatedPassword = '';
  createdTutorName = '';
  createdTutorEmail = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    this.tutorForm = this.fb.group({
      // Step 1: Basic Information
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      cin: ['', [Validators.required, Validators.pattern(/^[A-Z]{1,2}[0-9]{5,8}$/)]],
      dateOfBirth: ['', Validators.required],
      
      // Step 2: Address Information
      address: ['', Validators.required],
      city: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{4,5}$/)]],
      
      // Step 3: Professional Information
      yearsOfExperience: ['', [Validators.required, Validators.min(0), Validators.max(50)]],
      bio: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(500)]],
      
      // Password (generated automatically)
      password: ['']
    });
  }

  get f() {
    return this.tutorForm.controls;
  }

  nextStep(): void {
    if (this.currentStep === 1) {
      if (this.f['firstName'].invalid || this.f['lastName'].invalid || 
          this.f['email'].invalid || this.f['phone'].invalid || 
          this.f['cin'].invalid || this.f['dateOfBirth'].invalid) {
        this.markStepAsTouched(1);
        return;
      }
    } else if (this.currentStep === 2) {
      if (this.f['address'].invalid || this.f['city'].invalid || this.f['postalCode'].invalid) {
        this.markStepAsTouched(2);
        return;
      }
    }

    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  markStepAsTouched(step: number): void {
    if (step === 1) {
      this.f['firstName'].markAsTouched();
      this.f['lastName'].markAsTouched();
      this.f['email'].markAsTouched();
      this.f['phone'].markAsTouched();
      this.f['cin'].markAsTouched();
      this.f['dateOfBirth'].markAsTouched();
    } else if (step === 2) {
      this.f['address'].markAsTouched();
      this.f['city'].markAsTouched();
      this.f['postalCode'].markAsTouched();
    } else if (step === 3) {
      this.f['yearsOfExperience'].markAsTouched();
      this.f['bio'].markAsTouched();
    }
  }

  onSubmit(): void {
    if (this.tutorForm.invalid) {
      this.markStepAsTouched(3);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Generate a temporary password
    const tempPassword = this.generatePassword();
    this.tutorForm.patchValue({ password: tempPassword });

    this.userService.createTutor(this.tutorForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        this.generatedPassword = tempPassword;
        this.createdTutorName = `${this.tutorForm.value.firstName} ${this.tutorForm.value.lastName}`;
        this.createdTutorEmail = this.tutorForm.value.email;
        this.showPasswordModal = true;
      },
      error: (error) => {
        this.loading = false;
        console.error('Full error object:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.error);
        
        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error.message) {
          this.errorMessage = error.message;
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please make sure the backend is running on port 8081.';
        } else {
          this.errorMessage = `Failed to create tutor (Error ${error.status}). Please try again.`;
        }
      }
    });
  }

  generatePassword(): string {
    const length = 12;
    const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*';
    let password = '';
    for (let i = 0; i < length; i++) {
      password += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return password;
  }

  cancel(): void {
    if (confirm('Are you sure you want to cancel? All entered data will be lost.')) {
      this.router.navigate(['/dashboard/users/tutors']);
    }
  }

  getProgressPercentage(): number {
    return (this.currentStep / this.totalSteps) * 100;
  }

  closePasswordModal(): void {
    this.showPasswordModal = false;
    this.router.navigate(['/dashboard/users/tutors']);
  }
}
