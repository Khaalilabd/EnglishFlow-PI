import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Role-based access control guard
 * Redirects users to appropriate pages based on their role
 * Supports returnUrl for better UX
 */
export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const currentUser = authService.currentUserValue;

    // Check if user is authenticated
    if (!currentUser) {
      router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    // Check if user's role is allowed
    if (allowedRoles.includes(currentUser.role)) {
      return true;
    }

    // User doesn't have permission - redirect to their default page
    const defaultRoute = getDefaultRouteForRole(currentUser.role);
    router.navigate([defaultRoute], { 
      queryParams: { 
        error: 'insufficient_permissions',
        attempted: state.url 
      } 
    });

    return false;
  };
};

/**
 * Get default route based on user role
 * Centralized routing logic for better maintainability
 */
function getDefaultRouteForRole(role: string): string {
  const roleRoutes: { [key: string]: string } = {
    'STUDENT': '/user-panel',
    'TUTOR': '/tutor-panel',
    'TEACHER': '/tutor-panel', // TEACHER uses same panel as TUTOR
    'ADMIN': '/dashboard',
    'ACADEMIC_OFFICE_AFFAIR': '/dashboard'
  };

  return roleRoutes[role] || '/';
}
