import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockAuthResponse: AuthResponse = {
    id: 1,
    email: 'test@example.com',
    firstName: 'John',
    lastName: 'Doe',
    role: 'STUDENT',
    token: 'mock.jwt.token',
    refreshToken: 'mock-refresh-token',
    sessionToken: 'mock-session-token',
    type: 'Bearer'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    
    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    // Flush any pending session requests before verifying
    const sessionReqs = httpMock.match('http://localhost:8080/sessions/my-sessions');
    sessionReqs.forEach(req => req.flush([]));
    
    httpMock.verify();
    localStorage.clear();
  });

  // ========== BASIC CRUD TESTS ==========

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a new user', (done) => {
    const registerRequest: RegisterRequest = {
      email: 'newuser@example.com',
      password: 'Password123!',
      firstName: 'Jane',
      lastName: 'Smith',
      role: 'STUDENT'
    };

    service.register(registerRequest).subscribe(response => {
      expect(response).toEqual(mockAuthResponse);
      expect(localStorage.getItem('token')).toBe(mockAuthResponse.token);
      expect(localStorage.getItem('currentUser')).toBeTruthy();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(mockAuthResponse);
  });

  it('should login a user', (done) => {
    const loginRequest: LoginRequest = {
      email: 'test@example.com',
      password: 'Password123!'
    };

    service.login(loginRequest).subscribe(response => {
      expect(response).toEqual(mockAuthResponse);
      expect(service.isAuthenticated).toBe(true);
      expect(service.currentUserValue).toEqual(mockAuthResponse);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockAuthResponse);
  });

  // ========== COMPLEX BUSINESS LOGIC TESTS ==========

  it('should handle token refresh and update user data', (done) => {
    // Setup: user is already logged in
    localStorage.setItem('refreshToken', 'old-refresh-token');
    localStorage.setItem('token', 'old-token');

    const newAuthResponse: AuthResponse = {
      ...mockAuthResponse,
      token: 'new.jwt.token',
      refreshToken: 'new-refresh-token'
    };

    service.refreshToken().subscribe(response => {
      expect(response.token).toBe('new.jwt.token');
      expect(localStorage.getItem('token')).toBe('new.jwt.token');
      expect(localStorage.getItem('refreshToken')).toBe('new-refresh-token');
      
      // Handle the loadFreshUserData call
      const freshDataReq = httpMock.match('http://localhost:8080/api/users/1');
      if (freshDataReq.length > 0) {
        freshDataReq[0].flush(mockAuthResponse);
      }
      
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/refresh-token`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ refreshToken: 'old-refresh-token' });
    req.flush(newAuthResponse);
  });

  it('should preserve token when updating profile', (done) => {
    // Setup: user is logged in
    service['currentUserSubject'].next(mockAuthResponse);
    localStorage.setItem('token', mockAuthResponse.token);
    localStorage.setItem('refreshToken', mockAuthResponse.refreshToken!);

    const updateData = {
      firstName: 'Updated',
      lastName: 'Name',
      bio: 'New bio'
    };

    const updatedResponse = {
      ...mockAuthResponse,
      firstName: 'Updated',
      lastName: 'Name',
      bio: 'New bio'
    };

    service.updateProfile(updateData).subscribe(response => {
      expect(response.firstName).toBe('Updated');
      // CRITICAL: Token should be preserved
      expect(response.token).toBe(mockAuthResponse.token);
      expect(localStorage.getItem('token')).toBe(mockAuthResponse.token);
      done();
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/users/${mockAuthResponse.id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedResponse);
  });

  it('should handle logout and clear all auth data', (done) => {
    // Setup: user is logged in
    localStorage.setItem('token', mockAuthResponse.token);
    localStorage.setItem('refreshToken', mockAuthResponse.refreshToken!);
    localStorage.setItem('currentUser', JSON.stringify(mockAuthResponse));
    service['currentUserSubject'].next(mockAuthResponse);

    // Don't set sessionToken to avoid backend session cleanup call
    service.logout().subscribe(() => {
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('refreshToken')).toBeNull();
      expect(localStorage.getItem('currentUser')).toBeNull();
      expect(service.currentUserValue).toBeNull();
      expect(service.isAuthenticated).toBe(false);
      done();
    });
  });

  it('should check user roles correctly', () => {
    service['currentUserSubject'].next(mockAuthResponse);

    expect(service.hasRole(['STUDENT'])).toBe(true);
    expect(service.hasRole(['TUTOR'])).toBe(false);
    expect(service.hasRole(['STUDENT', 'TUTOR'])).toBe(true);
    expect(service.getUserRole()).toBe('STUDENT');
  });

  it('should handle password reset request', (done) => {
    const email = 'test@example.com';

    service.requestPasswordReset(email).subscribe(response => {
      expect(response).toBeTruthy();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password-reset/request`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email });
    req.flush({ message: 'Reset email sent' });
  });

  it('should handle password reset confirmation', (done) => {
    const token = 'reset-token-123';
    const newPassword = 'NewPassword123!';

    service.resetPassword(token, newPassword).subscribe(response => {
      expect(response).toBeTruthy();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password-reset/confirm`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ token, newPassword });
    req.flush({ message: 'Password reset successful' });
  });

  it('should activate account and set user data', (done) => {
    const activationToken = 'activation-token-123';

    service.activateAccount(activationToken).subscribe(response => {
      expect(response).toEqual(mockAuthResponse);
      expect(service.isAuthenticated).toBe(true);
      expect(localStorage.getItem('token')).toBe(mockAuthResponse.token);
      
      // Handle the loadFreshUserData call
      const freshDataReq = httpMock.match('http://localhost:8080/api/users/1');
      if (freshDataReq.length > 0) {
        freshDataReq[0].flush(mockAuthResponse);
      }
      
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/activate-api?token=${activationToken}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAuthResponse);
  });

  // ========== EDGE CASES & ERROR HANDLING ==========

  it('should handle login failure', (done) => {
    const loginRequest: LoginRequest = {
      email: 'wrong@example.com',
      password: 'WrongPassword'
    };

    service.login(loginRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(401);
        expect(service.isAuthenticated).toBe(false);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    req.flush({ message: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
  });

  it('should throw error when refreshing token without refresh token', () => {
    localStorage.removeItem('refreshToken');
    
    expect(() => {
      try {
        service.refreshToken();
      } catch (e: any) {
        throw e.message;
      }
    }).toThrow('No refresh token available');
  });

  it('should load user from localStorage on service initialization', () => {
    // Clear and reinitialize service
    localStorage.setItem('currentUser', JSON.stringify(mockAuthResponse));
    localStorage.setItem('token', mockAuthResponse.token);
    
    const newService = new AuthService(TestBed.inject(HttpTestingController) as any);
    
    expect(newService.currentUserValue).toEqual(mockAuthResponse);
    expect(newService.isAuthenticated).toBe(true);
  });

  it('should handle profile photo upload', (done) => {
    service['currentUserSubject'].next(mockAuthResponse);
    localStorage.setItem('token', mockAuthResponse.token);

    const formData = new FormData();
    formData.append('file', new Blob(['test']), 'photo.jpg');

    const responseWithPhoto = {
      ...mockAuthResponse,
      profilePhoto: 'http://localhost:8080/uploads/photo.jpg'
    };

    service.uploadProfilePhoto(mockAuthResponse.id, formData).subscribe(response => {
      expect(response.profilePhoto).toBe('http://localhost:8080/uploads/photo.jpg');
      expect(service.currentUserValue?.profilePhoto).toBe('http://localhost:8080/uploads/photo.jpg');
      done();
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/users/${mockAuthResponse.id}/profile-photo`);
    expect(req.request.method).toBe('POST');
    req.flush(responseWithPhoto);
  });

  it('should change password for authenticated user', (done) => {
    service['currentUserSubject'].next(mockAuthResponse);

    const currentPassword = 'OldPassword123!';
    const newPassword = 'NewPassword123!';

    service.changePassword(currentPassword, newPassword).subscribe(response => {
      expect(response).toBeTruthy();
      done();
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/users/${mockAuthResponse.id}/change-password`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ currentPassword, newPassword });
    req.flush({ message: 'Password changed successfully' });
  });

  it('should throw error when changing password without authentication', () => {
    service['currentUserSubject'].next(null);

    expect(() => {
      try {
        service.changePassword('old', 'new');
      } catch (e: any) {
        throw e.message;
      }
    }).toThrow('No user logged in');
  });
});
