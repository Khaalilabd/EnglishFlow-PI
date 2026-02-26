import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;  // Via API Gateway
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => {
        this.setCurrentUser(response);
      })
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        this.setCurrentUser(response);
      })
    );
  }

  logout(): Observable<void> {
    return new Observable(observer => {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      this.currentUserSubject.next(null);
      observer.next();
      observer.complete();
    });
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, { refreshToken }).pipe(
      tap(response => {
        this.setCurrentUser(response);
      })
    );
  }

  private setCurrentUser(user: AuthResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    localStorage.setItem('token', user.token);
    if (user.refreshToken) {
      localStorage.setItem('refreshToken', user.refreshToken);
    }
    this.currentUserSubject.next(user);
  }

  get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  updateCurrentUser(user: AuthResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  get isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  hasRole(roles: string[]): boolean {
    const currentUser = this.currentUserValue;
    return currentUser ? roles.includes(currentUser.role) : false;
  }

  getUserRole(): string | null {
    const currentUser = this.currentUserValue;
    return currentUser ? currentUser.role : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/password-reset/request`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/password-reset/confirm`, { token, newPassword });
  }

  activateAccount(token: string): Observable<AuthResponse> {
    // Utiliser l'endpoint activate-api qui retourne un AuthResponse avec le token
    return this.http.get<AuthResponse>(`${this.apiUrl}/activate-api?token=${token}`).pipe(
      tap(response => {
        this.setCurrentUser(response);
      })
    );
  }

  updateProfile(data: any): Observable<AuthResponse> {
    const currentUser = this.currentUserValue;
    if (!currentUser) {
      throw new Error('No user logged in');
    }
    
    // Utiliser l'endpoint /api/users/{id} via API Gateway
    return this.http.put<AuthResponse>(`${environment.apiUrl}/users/${currentUser.id}`, data).pipe(
      tap(response => {
        // Mettre à jour le currentUser avec les nouvelles données
        const updated = {
          ...currentUser,
          ...response
        };
        this.setCurrentUser(updated);
      })
    );
  }

  getAllUsers(): Observable<any[]> {
    // Appel via API Gateway (architecture microservices correcte)
    return this.http.get<any[]>(`${environment.apiUrl.replace('/api', '')}/public/users`);
  }

  uploadProfilePhoto(userId: number, formData: FormData): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/users/${userId}/profile-photo`, formData).pipe(
      tap(response => {
        const currentUser = this.currentUserValue;
        if (currentUser) {
          const updated = {
            ...currentUser,
            profilePhoto: response.profilePhoto
          };
          this.setCurrentUser(updated);
        }
      })
    );
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    const currentUser = this.currentUserValue;
    if (!currentUser) {
      throw new Error('No user logged in');
    }
    
    return this.http.post(`${environment.apiUrl}/users/${currentUser.id}/change-password`, {
      currentPassword,
      newPassword
    });
  }
}

