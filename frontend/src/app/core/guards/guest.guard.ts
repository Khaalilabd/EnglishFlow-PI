import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si l'utilisateur est déjà connecté, le rediriger vers sa page appropriée
  if (authService.isAuthenticated) {
    const currentUser = authService.currentUserValue;
    
    if (currentUser) {
      switch (currentUser.role) {
        case 'STUDENT':
          router.navigate(['/user-panel']);
          break;
        case 'TUTOR':
        case 'TEACHER':
          router.navigate(['/tutor-panel']);
          break;
        case 'ADMIN':
        case 'ACADEMIC_OFFICE_AFFAIR':
          router.navigate(['/dashboard']);
          break;
        default:
          router.navigate(['/']);
      }
    }
    
    return false;
  }

  return true;
};
