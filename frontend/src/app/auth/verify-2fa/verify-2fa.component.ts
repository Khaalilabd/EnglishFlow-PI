import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { TwoFactorAuthService } from '../../services/two-factor-auth.service';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';

@Component({
  selector: 'app-verify-2fa',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LogoComponent],
  templateUrl: './verify-2fa.component.html',
  styleUrls: ['./verify-2fa.component.scss']
})
export class Verify2FAComponent implements OnInit {
  twoFactorCode = '';
  tempToken = '';
  email = '';
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private twoFactorService: TwoFactorAuthService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.tempToken = params['tempToken'];
      this.email = params['email'];
      
      if (!this.tempToken) {
        this.router.navigate(['/login']);
      }
    });
  }

  verify2FA(): void {
    if (!this.twoFactorCode || this.twoFactorCode.length !== 6) {
      this.errorMessage = 'Please enter a valid 6-digit code';
      return;
    }

    this.loading = true;
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
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/login']);
  }
}
