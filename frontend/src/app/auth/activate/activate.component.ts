import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';

@Component({
  selector: 'app-activate',
  standalone: true,
  imports: [CommonModule, RouterModule, LogoComponent],
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.scss']
})
export class ActivateComponent implements OnInit {
  loading = true;
  success = false;
  errorMessage = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'] || '';
    
    if (!this.token) {
      this.loading = false;
      this.errorMessage = 'Invalid or missing activation token';
      return;
    }

    this.activateAccount();
  }

  activateAccount(): void {
    this.authService.activateAccount(this.token).subscribe({
      next: (response) => {
        this.success = true;
        this.loading = false;
        
        // Stocker les données utilisateur
        localStorage.setItem('currentUser', JSON.stringify(response));
        localStorage.setItem('token', response.token);
        this.authService.updateCurrentUser(response);
        
        // Vérifier si le profil est complet
        setTimeout(() => {
          // Si le profil n'est pas complet, rediriger vers complete-profile
          // Sinon, rediriger selon le rôle
          this.checkProfileCompletion(response);
        }, 2000);
      },
      error: (error) => {
        console.error('Activation error:', error);
        this.errorMessage = error.error?.message || 'Failed to activate account. The link may have expired.';
        this.loading = false;
      }
    });
  }

  private checkProfileCompletion(user: any): void {
    // Utiliser le champ profileCompleted retourné par l'API
    const isProfileComplete = user.profileCompleted === true;
    
    console.log('Profile completion check:', {
      profileCompleted: user.profileCompleted,
      isProfileComplete,
      user
    });
    
    if (!isProfileComplete) {
      // OAuth2 user - redirect to complete-profile
      this.router.navigate(['/auth/complete-profile'], {
        queryParams: {
          token: user.token,
          userId: user.id,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName
        }
      });
    } else {
      // Manual registration - profile already complete, redirect to home
      this.router.navigate(['/']);
    }
  }
}
