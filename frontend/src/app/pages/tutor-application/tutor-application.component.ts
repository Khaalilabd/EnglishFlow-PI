import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RecruitmentService, ApplicationResponse } from '../../core/services/recruitment.service';

@Component({
  selector: 'app-tutor-application',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  templateUrl: './tutor-application.component.html',
  styleUrls: ['./tutor-application.component.scss']
})
export class TutorApplicationComponent implements OnInit {
  currentStep = 1;
  totalSteps = 4;
  applicationId: number | null = null;
  
  step1Form!: FormGroup;
  step2Form!: FormGroup;
  step3Form!: FormGroup;
  
  uploadedDocuments: { [key: string]: File } = {};
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  
  // Terms and conditions
  termsAccepted = false;
  showTermsModal = false;
  showTermsError = false;
  
  // Animation states
  isButtonAnimating = false;

  constructor(
    private fb: FormBuilder,
    private recruitmentService: RecruitmentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForms();
  }

  initializeForms(): void {
    this.step1Form = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      cin: [''],
      dateOfBirth: [''],
      address: [''],
      city: [''],
      postalCode: [''],
      nationality: ['']
    });

    this.step2Form = this.fb.group({
      education: ['', Validators.required],
      certifications: [''],
      workExperience: [''],
      yearsOfExperience: [0, [Validators.required, Validators.min(0)]],
      englishLevel: ['', Validators.required],
      specializations: ['']
    });

    this.step3Form = this.fb.group({
      motivationLetter: ['', [Validators.required, Validators.minLength(100)]],
      teachingPhilosophy: ['', [Validators.required, Validators.minLength(50)]],
      availability: ['', Validators.required]
    });
  }

  nextStep(): void {
    this.isButtonAnimating = true;
    setTimeout(() => this.isButtonAnimating = false, 600);
    
    if (this.currentStep === 1 && this.step1Form.valid) {
      this.submitStep1();
    } else if (this.currentStep === 2 && this.step2Form.valid) {
      this.submitStep2();
    } else if (this.currentStep === 3 && this.step3Form.valid) {
      this.submitStep3();
    } else {
      this.markFormGroupTouched(this.getCurrentForm());
    }
  }

  previousStep(): void {
    this.isButtonAnimating = true;
    setTimeout(() => this.isButtonAnimating = false, 600);
    
    if (this.currentStep > 1) {
      this.currentStep--;
      this.errorMessage = '';
    }
  }
  
  goHome(): void {
    this.router.navigate(['/']);
  }

  submitStep1(): void {
    this.isSubmitting = true;
    this.errorMessage = '';

    this.recruitmentService.createApplication(this.step1Form.value).subscribe({
      next: (response: ApplicationResponse) => {
        this.applicationId = response.id;
        this.currentStep = 2;
        this.isSubmitting = false;
        this.successMessage = 'Step 1 completed!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to create application';
        this.isSubmitting = false;
      }
    });
  }

  submitStep2(): void {
    if (!this.applicationId) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    const data = {
      applicationId: this.applicationId,
      ...this.step2Form.value
    };

    this.recruitmentService.updateQualifications(data).subscribe({
      next: () => {
        this.currentStep = 3;
        this.isSubmitting = false;
        this.successMessage = 'Step 2 completed!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to update qualifications';
        this.isSubmitting = false;
      }
    });
  }

  submitStep3(): void {
    if (!this.applicationId) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    const data = {
      applicationId: this.applicationId,
      ...this.step3Form.value
    };

    this.recruitmentService.updatePresentation(data).subscribe({
      next: () => {
        this.currentStep = 4;
        this.isSubmitting = false;
        this.successMessage = 'Step 3 completed!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to update presentation';
        this.isSubmitting = false;
      }
    });
  }

  onFileSelected(event: any, documentType: string): void {
    const file = event.target.files[0];
    if (file) {
      this.uploadedDocuments[documentType] = file;
    }
  }

  uploadDocument(documentType: string): void {
    if (!this.applicationId || !this.uploadedDocuments[documentType]) return;

    const file = this.uploadedDocuments[documentType];
    
    // Validate file size (max 50MB for videos, 10MB for others)
    const maxSize = documentType === 'VIDEO_PRESENTATION' ? 50 * 1024 * 1024 : 10 * 1024 * 1024;
    if (file.size > maxSize) {
      this.errorMessage = `File too large. Maximum size is ${documentType === 'VIDEO_PRESENTATION' ? '50MB' : '10MB'}`;
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    console.log('Uploading document:', {
      applicationId: this.applicationId,
      documentType: documentType,
      fileName: file.name,
      fileSize: file.size,
      fileType: file.type
    });

    this.recruitmentService.uploadDocument(this.applicationId, file, documentType).subscribe({
      next: (response) => {
        console.log('Upload successful:', response);
        this.successMessage = `${documentType} uploaded successfully!`;
        setTimeout(() => this.successMessage = '', 3000);
        this.isSubmitting = false;
      },
      error: (error) => {
        console.error('Upload error:', error);
        const errorMsg = error.error?.message || error.message || `Failed to upload ${documentType}`;
        this.errorMessage = errorMsg;
        this.isSubmitting = false;
      }
    });
  }

  submitApplication(): void {
    if (!this.applicationId) return;

    // Check if terms are accepted
    if (!this.termsAccepted) {
      this.showTermsError = true;
      this.errorMessage = 'You must accept the terms and conditions to submit your application';
      setTimeout(() => {
        this.showTermsError = false;
        this.errorMessage = '';
      }, 5000);
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    // First, accept terms
    this.recruitmentService.acceptTerms(this.applicationId).subscribe({
      next: () => {
        // Then submit application
        this.recruitmentService.submitApplication(this.applicationId!).subscribe({
          next: () => {
            this.successMessage = 'Application submitted successfully!';
            setTimeout(() => {
              this.router.navigate(['/']);
            }, 2000);
          },
          error: (error: any) => {
            this.errorMessage = error.error?.message || 'Failed to submit application';
            this.isSubmitting = false;
          }
        });
      },
      error: (error: any) => {
        this.errorMessage = error.error?.message || 'Failed to accept terms';
        this.isSubmitting = false;
      }
    });
  }

  getCurrentForm(): FormGroup {
    switch (this.currentStep) {
      case 1: return this.step1Form;
      case 2: return this.step2Form;
      case 3: return this.step3Form;
      default: return this.step1Form;
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  getProgressPercentage(): number {
    return (this.currentStep / this.totalSteps) * 100;
  }
}
