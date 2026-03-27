import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SessionSearchRequest {
  userId?: number;
  status?: string;
  deviceType?: string;
  ipAddress?: string;
  country?: string;
  suspicious?: boolean;
  startDate?: string;
  endDate?: string;
  quickFilter?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

export interface SessionStatistics {
  totalSessions: number;
  activeSessions: number;
  suspiciousSessions: number;
  averageSessionDuration: number;
  sessionsToday: number;
  sessionsThisWeek: number;
  sessionsThisMonth: number;
  topCountries: { country: string; count: number }[];
  topDevices: { device: string; count: number }[];
}

@Injectable({
  providedIn: 'root'
})
export class AdminSessionService {
  private apiUrl = `${environment.apiUrl}/sessions/admin`;

  constructor(private http: HttpClient) {}

  searchSessions(request: SessionSearchRequest): Observable<any> {
    // Ensure page and size are always valid integers
    const cleanRequest: any = {
      page: request.page ?? 0,
      size: request.size ?? 20,
      sortBy: request.sortBy || 'lastActivity',
      sortDirection: request.sortDirection || 'DESC'
    };
    
    // Add optional fields only if they have values
    if (request.userId !== undefined && request.userId !== null) {
      cleanRequest.userId = request.userId;
    }
    if (request.status) {
      cleanRequest.status = request.status;
    }
    if (request.deviceType) {
      cleanRequest.deviceType = request.deviceType;
    }
    if (request.ipAddress) {
      cleanRequest.ipAddress = request.ipAddress;
    }
    if (request.country) {
      cleanRequest.country = request.country;
    }
    if (request.suspicious !== undefined && request.suspicious !== null) {
      cleanRequest.suspicious = request.suspicious;
    }
    if (request.startDate) {
      cleanRequest.startDate = request.startDate;
    }
    if (request.endDate) {
      cleanRequest.endDate = request.endDate;
    }
    if (request.quickFilter) {
      cleanRequest.quickFilter = request.quickFilter;
    }
    
    console.log('📤 Sending search request:', cleanRequest);
    
    return this.http.post<any>(`${this.apiUrl}/search`, cleanRequest);
  }

  getUserSessions(userId: number, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`, { params });
  }

  getSuspiciousSessions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/suspicious`);
  }

  terminateSession(sessionId: number, reason: string = 'ADMIN_TERMINATED'): Observable<any> {
    const params = new HttpParams().set('reason', reason);
    return this.http.delete<any>(`${this.apiUrl}/${sessionId}`, { params });
  }

  terminateAllUserSessions(userId: number, reason: string = 'ADMIN_TERMINATED'): Observable<any> {
    const params = new HttpParams().set('reason', reason);
    return this.http.delete<any>(`${this.apiUrl}/user/${userId}/all`, { params });
  }

  getSessionStatistics(days: number = 30): Observable<SessionStatistics> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<SessionStatistics>(`${this.apiUrl}/statistics`, { params });
  }

  getFilterOptions(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/filters`);
  }

  forceCleanup(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cleanup`, {});
  }
}
