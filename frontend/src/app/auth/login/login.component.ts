import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TwoFactorAuthService } from '../../services/two-factor-auth.service';
import { LogoComponent } from '../../shared/components/logo.component';
import { RecaptchaModule, RecaptchaFormsModule } from 'ng-recaptcha';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LogoComponent, RecaptchaModule, RecaptchaFormsModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  selectedRole: 'STUDENT' | 'TEACHER' = 'STUDENT';
  recaptchaToken: string | null = null;
  siteKey = '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'; // Google test key
  showPassword = false;
  
  // 2FA related
  show2FAModal = false;
  twoFactorCode = '';
  tempToken = '';
  loading2FA = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private twoFactorService: TwoFactorAuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  ngOnInit(): void {
    // Check for error messages from OAuth2 redirect
    this.route.queryParams.subscribe(params => {
      if (params['error'] === 'account_not_activated') {
        this.errorMessage = 'Your account is not activated yet. Please check your email for the activation link.';
        if (params['email']) {
          this.loginForm.patchValue({ email: params['email'] });
        }
      } else if (params['error']) {
        this.errorMessage = 'Authentication failed. Please try again.';
      }
    });
  }

  onCaptchaResolved(token: string | null): void {
    this.recaptchaToken = token;
    console.log('reCAPTCHA resolved:', token);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Add recaptcha token to login data (use test token if not provided)
    const loginData = {
      ...this.loginForm.value,
      recaptchaToken: this.recaptchaToken || 'test-token'
    };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('Login successful:', response);
        
        // Check if 2FA is required
        if (response.requires2FA && response.tempToken) {
          this.tempToken = response.tempToken;
          this.show2FAModal = true;
          this.loading = false;
          return;
        }
        
        // Vérifier si le profil est complet
        if (response.profileCompleted === false) {
          // Profil incomplet, rediriger vers complete-profile
          this.router.navigate(['/auth/complete-profile'], {
            queryParams: {
              token: response.token,
              userId: response.id,
              email: response.email,
              firstName: response.firstName,
              lastName: response.lastName
            }
          });
        } else {
          // Profil complet, rediriger selon le rôle
          if (response.role === 'ADMIN') {
            this.router.navigate(['/dashboard']); // Admin dashboard
          } else {
            this.router.navigate(['/']); // Landing page for students and tutors
          }
        }
      },
      error: (error) => {
        console.error('Login error:', error);
        this.errorMessage = error.error?.message || 'Invalid credentials';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }

  verify2FA(): void {
    if (!this.twoFactorCode || this.twoFactorCode.length !== 6) {
      this.errorMessage = 'Please enter a valid 6-digit code';
      return;
    }

    this.loading2FA = true;
    this.errorMessage = '';

    this.twoFactorService.verifyTwoFactorLogin(this.tempToken, this.twoFactorCode).subscribe({
      next: (response) => {
        console.log('2FA verification successful:', response);
        
        // Store the tokens and user data
        this.authService.updateCurrentUser(response);
        localStorage.setItem('token', response.token);
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
        
        // Redirect based on role
        if (response.role === 'ADMIN') {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        console.error('2FA verification error:', error);
        this.errorMessage = error.error?.message || 'Invalid verification code';
        this.loading2FA = false;
      },
      complete: () => {
        this.loading2FA = false;
      }
    });
  }

  cancel2FA(): void {
    this.show2FAModal = false;
    this.twoFactorCode = '';
    this.tempToken = '';
    this.errorMessage = '';
  }
}
