import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const currentUser = authService.currentUserValue;

    // Vérifier si l'utilisateur est connecté
    if (!currentUser) {
      router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    // Vérifier si le rôle de l'utilisateur est autorisé
    if (allowedRoles.includes(currentUser.role)) {
      return true;
    }

    // Rediriger vers la page appropriée selon le rôle
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

    return false;
  };
};
