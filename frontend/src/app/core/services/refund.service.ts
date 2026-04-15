import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Refund,
  CreateRefundRequest,
  RefundFilter,
  RefundStats
} from '../models/refund.model';

@Injectable({
  providedIn: 'root'
})
export class RefundService {
  private apiUrl = `${environment.apiUrl}/refunds`;

  constructor(private http: HttpClient) {}

  // Student operations
  createRefundRequest(request: CreateRefundRequest): Observable<Refund> {
    return this.http.post<Refund>(this.apiUrl, request);
  }

  getMyRefunds(): Observable<Refund[]> {
    return this.http.get<Refund[]>(`${this.apiUrl}/my-refunds`);
  }

  getRefundById(id: number): Observable<Refund> {
    return this.http.get<Refund>(`${this.apiUrl}/${id}`);
  }

  cancelRefund(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/cancel`);
  }

  // Admin operations
  getAllRefunds(filter?: RefundFilter): Observable<Refund[]> {
    let params = new HttpParams();
    
    if (filter) {
      if (filter.status) {
        params = params.set('status', filter.status);
      }
      if (filter.studentId) {
        params = params.set('studentId', filter.studentId.toString());
      }
      if (filter.startDate) {
        params = params.set('startDate', filter.startDate);
      }
      if (filter.endDate) {
        params = params.set('endDate', filter.endDate);
      }
      if (filter.itemType) {
        params = params.set('itemType', filter.itemType);
      }
    }

    return this.http.get<Refund[]>(this.apiUrl, { params });
  }

  approveRefund(id: number): Observable<Refund> {
    return this.http.put<Refund>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectRefund(id: number, reason: string): Observable<Refund> {
    return this.http.put<Refund>(`${this.apiUrl}/${id}/reject`, { reason });
  }

  getStatistics(): Observable<RefundStats> {
    return this.http.get<RefundStats>(`${this.apiUrl}/statistics`);
  }
}
