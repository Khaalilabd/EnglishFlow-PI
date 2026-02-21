import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { InvitationService } from '../../core/services/invitation.service';
import { PhoneInputComponent } from '../../shared/components/phone-input/phone-input.component';
import { CustomValidators } from '../../shared/validators/custom-validators';

interface InvitationDetails {
  id: number;
  email: string;
  role: string;
  expiryDate: string;
  used: boolean;
}

@Component({
  selector: 'app-accept-invitation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PhoneInputComponent],
  templateUrl: './accept-invitation.component.html',
  styleUrls: ['./accept-invitation.component.scss']
})
export class AcceptInvitationComponent implements OnInit {
  acceptForm: FormGroup;
  currentStep = 1;
  totalSteps = 3;
  loading = false;
  verifyingToken = true;
  errorMessage = '';
  invitationToken = '';
  invitationDetails: InvitationDetails | null = null;
  showPassword = false;
  showConfirmPassword = false;
  maxDate: string;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private invitationService: InvitationService
  ) {
    // Set max date to today
    const today = new Date();
    this.maxDate = today.toISOString().split('T')[0];
    
    this.acceptForm = this.fb.group({
      // Step 1: Personal Information
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(8), CustomValidators.strongPasswordValidator()]],
      confirmPassword: ['', Validators.required],
      
      // Step 2: Contact Information
      phone: ['', CustomValidators.phoneValidator()],
      cin: ['', [CustomValidators.cinValidator(), Validators.minLength(5), Validators.maxLength(20)]],
      dateOfBirth: ['', CustomValidators.minAgeValidator(18)],
      
      // Step 3: Address & Professional
      address: [''],
      city: [''],
      postalCode: ['', CustomValidators.postalCodeValidator()],
      bio: ['', Validators.maxLength(500)],
      yearsOfExperience: ['', [Validators.min(0), Validators.max(50)]]
    }, { 
      validators: CustomValidators.passwordMatchValidator('password', 'confirmPassword')
    });
  }

  ngOnInit(): void {
    // Get token from query params
    this.route.queryParams.subscribe(params => {
      this.invitationToken = params['token'];
      if (this.invitationToken) {
        this.verifyInvitation();
      } else {
        this.errorMessage = 'Invalid invitation link. Token is missing.';
        this.verifyingToken = false;
      }
    });
  }

  verifyInvitation(): void {
    this.verifyingToken = true;
    this.invitationService.verifyInvitation(this.invitationToken)
      .subscribe({
        next: (details) => {
          this.invitationDetails = details;
          this.verifyingToken = false;
          
          // Check if expired
          const expiryDate = new Date(details.expiryDate);
          if (expiryDate < new Date()) {
            this.errorMessage = 'This invitation has expired. Please contact the administrator for a new invitation.';
          }
        },
        error: (error) => {
          this.verifyingToken = false;
          if (error.status === 404) {
            this.errorMessage = 'Invalid invitation token.';
          } else if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else {
            this.errorMessage = 'Failed to verify invitation. Please try again.';
          }
        }
      });
  }

  get f() {
    return this.acceptForm.controls;
  }

  nextStep(): void {
    if (this.currentStep === 1) {
      if (this.f['firstName'].invalid || this.f['lastName'].invalid || 
          this.f['password'].invalid || this.f['confirmPassword'].invalid) {
        this.markStepAsTouched(1);
        return;
      }
      if (this.acceptForm.errors?.['passwordMismatch']) {
        return;
      }
    } else if (this.currentStep === 2) {
      if (this.f['cin'].invalid || this.f['dateOfBirth'].invalid) {
        this.markStepAsTouched(2);
        return;
      }
    }

    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
    }
  }

  get passwordStrength(): string {
    const password = this.f['password']?.value || '';
    if (password.length === 0) return '';
    if (password.length < 8) return 'weak';
    
    let strength = 0;
    if (/[A-Z]/.test(password)) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    if (strength <= 2) return 'weak';
    if (strength === 3) return 'medium';
    return 'strong';
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
      this.f['password'].markAsTouched();
      this.f['confirmPassword'].markAsTouched();
    } else if (step === 2) {
      this.f['phone'].markAsTouched();
      this.f['cin'].markAsTouched();
      this.f['dateOfBirth'].markAsTouched();
    } else if (step === 3) {
      this.f['address'].markAsTouched();
      this.f['city'].markAsTouched();
      this.f['postalCode'].markAsTouched();
      this.f['bio'].markAsTouched();
      this.f['yearsOfExperience'].markAsTouched();
    }
  }

  onSubmit(): void {
    if (this.acceptForm.invalid) {
      this.markStepAsTouched(3);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.acceptForm.value;
    const requestData = {
      token: this.invitationToken,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      password: formValue.password,
      phone: formValue.phone || null,
      cin: formValue.cin || null,
      dateOfBirth: formValue.dateOfBirth || null,
      address: formValue.address || null,
      city: formValue.city || null,
      postalCode: formValue.postalCode || null,
      bio: formValue.bio || null,
      yearsOfExperience: formValue.yearsOfExperience || null
    };

    this.invitationService.acceptInvitation(requestData)
      .subscribe({
        next: (response) => {
          this.loading = false;
          
          // Pour TUTOR/ACADEMIC: rediriger vers la page Angular statique (activation par admin)
          this.router.navigate(['/activation-pending'], {
            queryParams: {
              email: this.invitationDetails?.email,
              firstName: formValue.firstName,
              type: 'admin'
            }
          });
        },
        error: (error) => {
          this.loading = false;
          console.error('Error accepting invitation:', error);
          
          if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else if (error.status === 0) {
            this.errorMessage = 'Cannot connect to server. Please try again later.';
          } else {
            this.errorMessage = 'Failed to create account. Please try again.';
          }
        }
      });
  }

  getRedirectUrl(role: string): string {
    const roleRoutes: { [key: string]: string } = {
      'TUTOR': '/tutor-panel',
      'TEACHER': '/tutor-panel',
      'ACADEMIC_OFFICE_AFFAIR': '/dashboard',
      'ADMIN': '/dashboard'
    };
    return roleRoutes[role] || '/';
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  getProgressPercentage(): number {
    return (this.currentStep / this.totalSteps) * 100;
  }

  getRoleName(role: string): string {
    const roleNames: { [key: string]: string } = {
      'TUTOR': 'Tutor',
      'TEACHER': 'Teacher',
      'ACADEMIC_OFFICE_AFFAIR': 'Academic Affairs Staff',
      'ADMIN': 'Administrator'
    };
    return roleNames[role] || role;
  }
}
