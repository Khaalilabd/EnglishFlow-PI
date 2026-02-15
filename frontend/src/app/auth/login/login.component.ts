import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';
import { RecaptchaModule, RecaptchaFormsModule } from 'ng-recaptcha';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LogoComponent, RecaptchaModule, RecaptchaFormsModule],
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

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
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
        // Redirect based on user role
        if (response.role === 'ADMIN') {
          this.router.navigate(['/dashboard']); // Admin dashboard
        } else {
          this.router.navigate(['/']); // Landing page for students and tutors
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
}
