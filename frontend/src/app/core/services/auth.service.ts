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

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  private setCurrentUser(user: AuthResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    localStorage.setItem('token', user.token);
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
    return this.http.put<AuthResponse>(`http://localhost:8080/api/users/${currentUser.id}`, data).pipe(
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
    // Utiliser l'endpoint public via API Gateway
    return this.http.get<any[]>('http://localhost:8080/public/users');
  }
}
