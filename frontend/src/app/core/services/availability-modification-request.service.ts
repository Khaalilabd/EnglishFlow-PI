import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AvailabilityModificationRequest {
  id?: number;
  tutorId: number;
  tutorName: string;
  tutorEmail?: string;
  reason: string;
  proposedAvailability?: string;
  status?: string;
  requestedAt?: Date;
  reviewedAt?: Date;
  reviewerId?: number;
  reviewerName?: string;
  reviewComment?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AvailabilityModificationRequestService {
  private apiUrl = `${environment.apiUrl}/courses/availability-modification-requests`;

  constructor(private http: HttpClient) {}

  createRequest(request: AvailabilityModificationRequest): Observable<AvailabilityModificationRequest> {
    return this.http.post<AvailabilityModificationRequest>(this.apiUrl, request);
  }

  getAllRequests(): Observable<AvailabilityModificationRequest[]> {
    return this.http.get<AvailabilityModificationRequest[]>(this.apiUrl);
  }

  getRequestsByTutor(tutorId: number): Observable<AvailabilityModificationRequest[]> {
    return this.http.get<AvailabilityModificationRequest[]>(`${this.apiUrl}/tutor/${tutorId}`);
  }

  getPendingRequests(): Observable<AvailabilityModificationRequest[]> {
    return this.http.get<AvailabilityModificationRequest[]>(`${this.apiUrl}/pending`);
  }

  approveRequest(requestId: number, reviewerId: number, reviewerName: string, comment: string): Observable<AvailabilityModificationRequest> {
    return this.http.put<AvailabilityModificationRequest>(`${this.apiUrl}/${requestId}/approve`, {
      reviewerId,
      reviewerName,
      comment
    });
  }

  rejectRequest(requestId: number, reviewerId: number, reviewerName: string, comment: string): Observable<AvailabilityModificationRequest> {
    return this.http.put<AvailabilityModificationRequest>(`${this.apiUrl}/${requestId}/reject`, {
      reviewerId,
      reviewerName,
      comment
    });
  }
}
