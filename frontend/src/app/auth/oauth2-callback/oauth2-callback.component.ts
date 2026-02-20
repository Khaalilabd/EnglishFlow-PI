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
    // Get params from both query params and fragment (in case of hash routing issues)
    this.route.queryParams.subscribe(params => {
      // Also check fragment in case URL got mangled
      const fragment = this.route.snapshot.fragment;
      let actualParams = { ...params };
      
      // If fragment contains query params (OAuth2 redirect issue), parse them
      if (fragment && fragment.includes('?')) {
        const fragmentParams = new URLSearchParams(fragment.split('?')[1]);
        fragmentParams.forEach((value, key) => {
          if (!actualParams[key]) {
            actualParams[key] = value;
          }
        });
      }
      
      const token = actualParams['token'];
      const id = actualParams['id'] ? parseInt(actualParams['id'], 10) : 0;
      const email = actualParams['email'];
      const firstName = actualParams['firstName'];
      const lastName = actualParams['lastName'];
      const role = actualParams['role'] || 'STUDENT';
      const profileCompleted = actualParams['profileCompleted'] === 'true';

      console.log('OAuth2 Callback - Received params:', { token: token ? 'present' : 'missing', id, email, role, profileCompleted });

      if (token && email && id) {
        // Store user data
        const userData = {
          token,
          type: 'Bearer',
          id,
          email,
          firstName,
          lastName,
          role,
          profilePhoto: actualParams['profilePhoto'] || null,
          phone: actualParams['phone'] || null
        };
        
        localStorage.setItem('currentUser', JSON.stringify(userData));
        localStorage.setItem('token', token);
        
        // Update auth service subject to trigger navbar update
        this.authService['currentUserSubject'].next(userData);
        
        console.log('OAuth2 Callback - User authenticated, redirecting...');
        
        // Redirect based on profile completion
        setTimeout(() => {
          if (!profileCompleted) {
            // Profil incomplet, rediriger vers complete-profile
            console.log('OAuth2 Callback - Profile incomplete, redirecting to complete-profile');
            this.router.navigate(['/auth/complete-profile'], {
              queryParams: {
                token,
                userId: id,
                email,
                firstName,
                lastName
              }
            });
          } else {
            // Profil complet, rediriger vers la home page
            console.log('OAuth2 Callback - Profile complete, redirecting to home');
            this.router.navigate(['/']);
          }
        }, 1000);
      } else {
        // Error - redirect to login
        console.error('OAuth2 Callback - Missing required params:', { token: !!token, email: !!email, id: !!id });
        this.router.navigate(['/login'], { 
          queryParams: { error: 'OAuth2 authentication failed. Missing required parameters.' } 
        });
      }
    });
  }
}
