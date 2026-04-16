import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TwoFactorAuthService, TwoFactorSetupResponse, TwoFactorStatusResponse } from './two-factor-auth.service';
import { environment } from '../../environments/environment';

describe('TwoFactorAuthService', () => {
  let service: TwoFactorAuthService;
  let httpMock: HttpTestingController;

  const mockSetupResponse: TwoFactorSetupResponse = {
    secret: 'JBSWY3DPEHPK3PXP',
    qrCodeUrl: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...',
    backupCodes: [
      '12345678',
      '23456789',
      '34567890',
      '45678901',
      '56789012'
    ],
    message: '2FA setup initiated'
  };

  const mockStatusResponse: TwoFactorStatusResponse = {
    enabled: true,
    enabledAt: '2026-04-01T10:00:00',
    lastUsedAt: '2026-04-15T08:30:00',
    backupCodesRemaining: 3
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TwoFactorAuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TwoFactorAuthService);
    httpMock = TestBed.inject(HttpTestingController);
    
    localStorage.setItem('token', 'mock-token');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ========== BASIC CRUD TESTS ==========

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get 2FA status', (done) => {
    service.getTwoFactorStatus().subscribe(status => {
      expect(status).toEqual(mockStatusResponse);
      expect(status.enabled).toBe(true);
      expect(status.backupCodesRemaining).toBe(3);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStatusResponse);
  });

  // ========== COMPLEX BUSINESS LOGIC TESTS ==========

  it('should setup 2FA and generate QR code with backup codes', (done) => {
    service.setupTwoFactor().subscribe(response => {
      expect(response).toEqual(mockSetupResponse);
      expect(response.secret).toBeTruthy();
      expect(response.qrCodeUrl).toContain('data:image/png');
      expect(response.backupCodes.length).toBe(5);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/setup`);
    expect(req.request.method).toBe('POST');
    req.flush(mockSetupResponse);
  });

  it('should enable 2FA after verifying TOTP code', (done) => {
    const totpCode = '123456';

    service.enableTwoFactor(totpCode).subscribe(response => {
      expect(response.message).toBe('2FA enabled successfully');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/enable`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ code: totpCode });
    req.flush({ message: '2FA enabled successfully' });
  });

  it('should disable 2FA with code verification', (done) => {
    const totpCode = '654321';

    service.disableTwoFactor(totpCode).subscribe(response => {
      expect(response.message).toBe('2FA disabled successfully');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/disable`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ code: totpCode });
    req.flush({ message: '2FA disabled successfully' });
  });

  it('should regenerate backup codes', (done) => {
    const newBackupCodes = [
      '11111111',
      '22222222',
      '33333333',
      '44444444',
      '55555555'
    ];

    service.regenerateBackupCodes().subscribe(codes => {
      expect(codes.length).toBe(5);
      expect(codes).toEqual(newBackupCodes);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/backup-codes/regenerate`);
    expect(req.request.method).toBe('POST');
    req.flush(newBackupCodes);
  });

  it('should verify 2FA code during login flow', (done) => {
    const tempToken = 'temp-login-token-123';
    const totpCode = '789012';

    const loginResponse = {
      token: 'final-jwt-token',
      refreshToken: 'refresh-token',
      user: { id: 1, email: 'test@example.com' }
    };

    service.verifyTwoFactorLogin(tempToken, totpCode).subscribe(response => {
      expect(response.token).toBe('final-jwt-token');
      expect(response.user.email).toBe('test@example.com');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login/verify-2fa`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ tempToken, code: totpCode });
    req.flush(loginResponse);
  });

  it('should handle complete 2FA setup workflow', (done) => {
    // Step 1: Setup 2FA
    service.setupTwoFactor().subscribe(setupResponse => {
      expect(setupResponse.secret).toBeTruthy();
      expect(setupResponse.backupCodes.length).toBe(5);

      // Step 2: Enable 2FA with verification
      service.enableTwoFactor('123456').subscribe(enableResponse => {
        expect(enableResponse.message).toContain('enabled');

        // Step 3: Verify status is now enabled
        service.getTwoFactorStatus().subscribe(status => {
          expect(status.enabled).toBe(true);
          done();
        });

        const statusReq = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
        statusReq.flush({ ...mockStatusResponse, enabled: true });
      });

      const enableReq = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/enable`);
      enableReq.flush({ message: '2FA enabled successfully' });
    });

    const setupReq = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/setup`);
    setupReq.flush(mockSetupResponse);
  });

  it('should track backup code usage', (done) => {
    // Initial status: 5 backup codes
    service.getTwoFactorStatus().subscribe(initialStatus => {
      expect(initialStatus.backupCodesRemaining).toBe(5);

      // After using one backup code during login
      service.getTwoFactorStatus().subscribe(updatedStatus => {
        expect(updatedStatus.backupCodesRemaining).toBe(4);
        done();
      });

      const updatedReq = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
      updatedReq.flush({ ...mockStatusResponse, backupCodesRemaining: 4 });
    });

    const initialReq = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
    initialReq.flush({ ...mockStatusResponse, backupCodesRemaining: 5 });
  });

  it('should warn when backup codes are running low', (done) => {
    const lowBackupStatus: TwoFactorStatusResponse = {
      ...mockStatusResponse,
      backupCodesRemaining: 1
    };

    service.getTwoFactorStatus().subscribe(status => {
      expect(status.backupCodesRemaining).toBe(1);
      // In real app, this would trigger a warning to regenerate codes
      expect(status.backupCodesRemaining).toBeLessThan(2);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
    req.flush(lowBackupStatus);
  });

  // ========== EDGE CASES & ERROR HANDLING ==========

  it('should handle invalid TOTP code during enable', (done) => {
    const invalidCode = '000000';

    service.enableTwoFactor(invalidCode).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        expect(error.error.message).toContain('Invalid');
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/enable`);
    req.flush(
      { message: 'Invalid verification code' },
      { status: 400, statusText: 'Bad Request' }
    );
  });

  it('should handle expired TOTP code', (done) => {
    const expiredCode = '123456';

    service.verifyTwoFactorLogin('temp-token', expiredCode).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(401);
        expect(error.error.message).toContain('expired');
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login/verify-2fa`);
    req.flush(
      { message: 'Code expired or invalid' },
      { status: 401, statusText: 'Unauthorized' }
    );
  });

  it('should handle disabling 2FA when not enabled', (done) => {
    service.disableTwoFactor('123456').subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/disable`);
    req.flush(
      { message: '2FA is not enabled' },
      { status: 400, statusText: 'Bad Request' }
    );
  });

  it('should handle regenerating backup codes when 2FA is disabled', (done) => {
    service.regenerateBackupCodes().subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(403);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/backup-codes/regenerate`);
    req.flush(
      { message: '2FA must be enabled first' },
      { status: 403, statusText: 'Forbidden' }
    );
  });

  it('should include authorization header in all requests', () => {
    service.getTwoFactorStatus().subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    expect(req.request.headers.get('Content-Type')).toBe('application/json');
    req.flush(mockStatusResponse);
  });

  it('should handle network errors gracefully', (done) => {
    service.setupTwoFactor().subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(0);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/setup`);
    req.error(new ProgressEvent('Network error'));
  });

  it('should validate backup code format', (done) => {
    const invalidBackupCode = 'abc'; // Too short

    service.verifyTwoFactorLogin('temp-token', invalidBackupCode).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login/verify-2fa`);
    req.flush(
      { message: 'Invalid code format' },
      { status: 400, statusText: 'Bad Request' }
    );
  });

  it('should handle status check for user without 2FA', (done) => {
    const disabledStatus: TwoFactorStatusResponse = {
      enabled: false,
      backupCodesRemaining: 0
    };

    service.getTwoFactorStatus().subscribe(status => {
      expect(status.enabled).toBe(false);
      expect(status.enabledAt).toBeUndefined();
      expect(status.lastUsedAt).toBeUndefined();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/2fa/status`);
    req.flush(disabledStatus);
  });
});
