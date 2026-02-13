import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-oauth2-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary via-primary-dark to-accent-navy">
      <div class="text-center">
        <div class="inline-block">
          <svg class="animate-spin h-16 w-16 text-secondary" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
        <h2 class="text-2xl font-black text-secondary mt-6">Completing sign in...</h2>
        <p class="text-secondary/80 mt-2">Please wait a moment</p>
      </div>
    </div>
  `
})
export class OAuth2CallbackComponent implements OnInit {
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const email = params['email'];
      const firstName = params['firstName'];
      const lastName = params['lastName'];
      const role = params['role'] || 'STUDENT';

      if (token && email) {
        // Store user data
        const userData = {
          token,
          id: 0, // Will be set by backend
          email,
          firstName,
          lastName,
          role
        };
        
        localStorage.setItem('currentUser', JSON.stringify(userData));
        localStorage.setItem('token', token);
        
        // Redirect based on role
        setTimeout(() => {
          if (role === 'TEACHER') {
            // Teachers go to dashboard
            this.router.navigate(['/dashboard']);
          } else {
            // Students go to landing page
            this.router.navigate(['/']);
          }
        }, 1000);
      } else {
        // Error - redirect to login
        this.router.navigate(['/login'], { 
          queryParams: { error: 'OAuth2 authentication failed' } 
        });
      }
    });
  }
}
