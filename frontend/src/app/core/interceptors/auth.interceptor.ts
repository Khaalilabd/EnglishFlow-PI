import { HttpInterceptorFn, HttpErrorResponse, HttpRequest, HttpEvent, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError, BehaviorSubject, filter, take, switchMap, Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  
  // Add auth header if user is logged in
  const token = authService.getToken();
  
  // Debug logging
  if (req.url.includes('/users/')) {
    console.log('🔍 Interceptor - Request URL:', req.url);
    console.log('🔍 Interceptor - Token exists:', !!token);
    console.log('🔍 Interceptor - Token length:', token?.length);
    console.log('🔍 Interceptor - Token format valid:', token?.split('.').length === 3);
  }
  
  // Only add token if it's a valid JWT format
  if (token && token.split('.').length === 3) {
    req = addTokenHeader(req, token);
  } else if (token) {
    console.error('❌ Invalid token format, not adding to request:', token);
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 error and we have a refresh token, try to refresh
      if (error.status === 401 && authService.getRefreshToken() && !req.url.includes('/auth/refresh')) {
        return handle401Error(req, next, authService);
      }
      
      return throwError(() => error);
    })
  );
};

function addTokenHeader(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return request.clone({
    headers: request.headers.set('Authorization', `Bearer ${token}`)
  });
}

function handle401Error(request: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthService): Observable<HttpEvent<unknown>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((response: any) => {
        isRefreshing = false;
        refreshTokenSubject.next(response.token);
        
        return next(addTokenHeader(request, response.token));
      }),
      catchError((error) => {
        isRefreshing = false;
        
        // If refresh fails, logout user
        authService.logout().subscribe();
        
        return throwError(() => error);
      })
    );
  }

  // If already refreshing, wait for the new token
  return refreshTokenSubject.pipe(
    filter(token => token !== null),
    take(1),
    switchMap((token) => next(addTokenHeader(request, token)))
  );
}