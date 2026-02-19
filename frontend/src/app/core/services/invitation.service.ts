import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface InvitationRequest {
  email: string;
  role: 'TUTOR' | 'ACADEMIC_OFFICE_AFFAIR';
}

export interface InvitationResponse {
  id: number;
  email: string;
  role: string;
  expiryDate: string;
  used: boolean;
  invitedBy: number;
  createdAt: string;
  usedAt: string | null;
  token?: string; // Optional - only returned in some endpoints
}

export interface AcceptInvitationRequest {
  token: string;
  firstName: string;
  lastName: string;
  password: string;
  phone?: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  yearsOfExperience?: number;
}

export interface AcceptInvitationResponse {
  token: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  profilePhoto: string | null;
  phone: string | null;
  profileCompleted: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class InvitationService {
  private apiUrl = `${environment.apiUrl}/invitations`;

  constructor(private http: HttpClient) {}

  /**
   * Send an invitation to a user
   * @param request Invitation request with email and role
   */
  sendInvitation(request: InvitationRequest): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/send`, request);
  }

  /**
   * Verify an invitation token
   * @param token Invitation token from email
   */
  verifyInvitation(token: string): Observable<InvitationResponse> {
    return this.http.get<InvitationResponse>(`${this.apiUrl}/token/${token}`);
  }

  /**
   * Accept an invitation and create account
   * @param request Accept invitation request with user details
   */
  acceptInvitation(request: AcceptInvitationRequest): Observable<AcceptInvitationResponse> {
    return this.http.post<AcceptInvitationResponse>(`${this.apiUrl}/accept`, request);
  }

  /**
   * Get all invitations (admin only)
   */
  getAllInvitations(): Observable<InvitationResponse[]> {
    return this.http.get<InvitationResponse[]>(this.apiUrl);
  }

  /**
   * Get pending invitations (admin only)
   */
  getPendingInvitations(): Observable<InvitationResponse[]> {
    return this.http.get<InvitationResponse[]>(`${this.apiUrl}/pending`);
  }

  /**
   * Resend an invitation (admin only)
   * @param invitationId ID of the invitation to resend
   */
  resendInvitation(invitationId: number): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/${invitationId}/resend`, {});
  }

  /**
   * Cancel an invitation (admin only)
   * @param invitationId ID of the invitation to cancel
   */
  cancelInvitation(invitationId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${invitationId}`);
  }

  /**
   * Cleanup expired invitations (admin only)
   */
  cleanupExpiredInvitations(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/cleanup`, {});
  }

  /**
   * Check if invitation is expired
   * @param expiryDate Expiry date string
   */
  isExpired(expiryDate: string): boolean {
    return new Date(expiryDate) < new Date();
  }

  /**
   * Get days until expiry
   * @param expiryDate Expiry date string
   */
  getDaysUntilExpiry(expiryDate: string): number {
    const expiry = new Date(expiryDate);
    const now = new Date();
    const diffTime = expiry.getTime() - now.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  /**
   * Format role name for display
   * @param role Role string
   */
  formatRoleName(role: string): string {
    const roleNames: { [key: string]: string } = {
      'TUTOR': 'Tutor',
      'TEACHER': 'Teacher',
      'ACADEMIC_OFFICE_AFFAIR': 'Academic Affairs',
      'ADMIN': 'Administrator'
    };
    return roleNames[role] || role;
  }
}
