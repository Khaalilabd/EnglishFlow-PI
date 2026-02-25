import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ClubUpdateRequest {
  id: number;
  clubId: number;
  requestedBy: number;
  name: string;
  description: string;
  objective: string;
  category: string;
  maxMembers: number;
  image?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  vicePresidentApproved: boolean;
  secretaryApproved: boolean;
  createdAt: string;
  appliedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClubUpdateRequestService {
  private apiUrl = `${environment.apiUrl}/club-update-requests`;

  constructor(private http: HttpClient) {}

  getPendingRequestsForClub(clubId: number): Observable<ClubUpdateRequest[]> {
    return this.http.get<ClubUpdateRequest[]>(`${this.apiUrl}/club/${clubId}/pending`);
  }

  getAllRequestsForClub(clubId: number): Observable<ClubUpdateRequest[]> {
    return this.http.get<ClubUpdateRequest[]>(`${this.apiUrl}/club/${clubId}`);
  }

  getRequestById(requestId: number): Observable<ClubUpdateRequest> {
    return this.http.get<ClubUpdateRequest>(`${this.apiUrl}/${requestId}`);
  }

  approveRequest(requestId: number, approverId: number): Observable<ClubUpdateRequest> {
    return this.http.post<ClubUpdateRequest>(
      `${this.apiUrl}/${requestId}/approve?approverId=${approverId}`,
      {}
    );
  }

  rejectRequest(requestId: number, rejecterId: number): Observable<ClubUpdateRequest> {
    return this.http.post<ClubUpdateRequest>(
      `${this.apiUrl}/${requestId}/reject?rejecterId=${rejecterId}`,
      {}
    );
  }
}
