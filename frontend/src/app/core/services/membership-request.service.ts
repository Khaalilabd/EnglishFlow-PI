import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MembershipRequest, CreateMembershipRequest } from '../models/club.model';

@Injectable({
  providedIn: 'root'
})
export class MembershipRequestService {
  private apiUrl = `${environment.apiUrl}/membership-requests`;

  constructor(private http: HttpClient) {}

  createRequest(request: CreateMembershipRequest): Observable<MembershipRequest> {
    return this.http.post<MembershipRequest>(this.apiUrl, request);
  }

  getPendingRequestsForClub(clubId: number): Observable<MembershipRequest[]> {
    return this.http.get<MembershipRequest[]>(`${this.apiUrl}/club/${clubId}/pending`);
  }

  getRequestsByClub(clubId: number): Observable<MembershipRequest[]> {
    return this.http.get<MembershipRequest[]>(`${this.apiUrl}/club/${clubId}/all`);
  }

  getTotalPayments(clubId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/total-payments`);
  }

  getUserRequests(userId: number): Observable<MembershipRequest[]> {
    return this.http.get<MembershipRequest[]>(`${this.apiUrl}/user/${userId}`);
  }

  approveRequest(requestId: number, reviewerId: number): Observable<MembershipRequest> {
    const params = new HttpParams().set('reviewerId', reviewerId.toString());
    return this.http.post<MembershipRequest>(`${this.apiUrl}/${requestId}/approve`, null, { params });
  }

  rejectRequest(requestId: number, reviewerId: number, comment?: string): Observable<MembershipRequest> {
    const params = new HttpParams().set('reviewerId', reviewerId.toString());
    const body = comment ? { comment } : {};
    return this.http.post<MembershipRequest>(`${this.apiUrl}/${requestId}/reject`, body, { params });
  }
}
