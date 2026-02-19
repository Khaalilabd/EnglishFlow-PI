import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';
import { CustomValidators } from '../../shared/validators/custom-validators';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LogoComponent],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  token: string = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.resetPasswordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8), CustomValidators.strongPasswordValidator()]],
      confirmPassword: ['', [Validators.required]]
    }, { 
      validators: CustomValidators.passwordMatchValidator('password', 'confirmPassword')
    });
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'] || '';
    if (!this.token) {
      this.errorMessage = 'Invalid or missing reset token';
    }
  }

  get passwordStrength(): string {
    const password = this.password?.value || '';
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

  onSubmit(): void {
    if (this.resetPasswordForm.invalid || !this.token) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.resetPassword(this.token, this.resetPasswordForm.value.password).subscribe({
      next: () => {
        this.successMessage = 'Password reset successful! Redirecting to login...';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Password reset error:', error);
        this.errorMessage = error.error?.message || 'An error occurred. Please try again.';
        this.loading = false;
      }
    });
  }

  get password() {
    return this.resetPasswordForm.get('password');
  }

  get confirmPassword() {
    return this.resetPasswordForm.get('confirmPassword');
  }
}
