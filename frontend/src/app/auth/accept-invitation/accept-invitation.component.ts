import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

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
  imports: [CommonModule, ReactiveFormsModule],
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

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
    this.acceptForm = this.fb.group({
      // Step 1: Personal Information
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      
      // Step 2: Contact Information
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      cin: ['', Validators.pattern(/^[A-Z]{1,2}[0-9]{5,8}$/)],
      dateOfBirth: [''],
      
      // Step 3: Address & Professional
      address: [''],
      city: [''],
      postalCode: ['', Validators.pattern(/^[0-9]{4,5}$/)],
      bio: ['', Validators.maxLength(500)],
      yearsOfExperience: ['', [Validators.min(0), Validators.max(50)]]
    }, { validators: this.passwordMatchValidator });
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

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  verifyInvitation(): void {
    this.verifyingToken = true;
    this.http.get<InvitationDetails>(`${environment.apiUrl}/invitations/token/${this.invitationToken}`)
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
      if (this.acceptForm.hasError('passwordMismatch')) {
        return;
      }
    } else if (this.currentStep === 2) {
      if (this.f['phone'].invalid) {
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

    this.http.post<any>(`${environment.apiUrl}/invitations/accept`, requestData)
      .subscribe({
        next: (response) => {
          this.loading = false;
          
          // Don't store token or login - account needs activation
          // Redirect to pending activation page
          this.router.navigate(['/activation-pending'], {
            queryParams: {
              email: this.invitationDetails?.email,
              firstName: formValue.firstName
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
