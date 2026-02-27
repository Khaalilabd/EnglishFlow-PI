import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TwoFactorSetupResponse {
  secret: string;
  qrCodeUrl: string;
  backupCodes: string[];
  message: string;
}

export interface TwoFactorStatusResponse {
  enabled: boolean;
  enabledAt?: string;
  lastUsedAt?: string;
  backupCodesRemaining: number;
}

export interface TwoFactorVerifyRequest {
  code: string;
}

@Injectable({
  providedIn: 'root'
})
export class TwoFactorAuthService {
  private apiUrl = `${environment.apiUrl}/auth/2fa`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Initialize 2FA setup - generates secret and QR code
   */
  setupTwoFactor(): Observable<TwoFactorSetupResponse> {
    return this.http.post<TwoFactorSetupResponse>(
      `${this.apiUrl}/setup`,
      {},
      { headers: this.getHeaders() }
    );
  }

  /**
   * Enable 2FA after verifying the code
   */
  enableTwoFactor(code: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/enable`,
      { code },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Disable 2FA with code verification
   */
  disableTwoFactor(code: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/disable`,
      { code },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Get 2FA status for current user
   */
  getTwoFactorStatus(): Observable<TwoFactorStatusResponse> {
    return this.http.get<TwoFactorStatusResponse>(
      `${this.apiUrl}/status`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Regenerate backup codes
   */
  regenerateBackupCodes(): Observable<string[]> {
    return this.http.post<string[]>(
      `${this.apiUrl}/backup-codes/regenerate`,
      {},
      { headers: this.getHeaders() }
    );
  }

  /**
   * Verify 2FA code during login
   */
  verifyTwoFactorLogin(tempToken: string, code: string): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/auth/login/verify-2fa`,
      { tempToken, code }
    );
  }
}
