import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserSession {
  id: number;
  sessionToken: string;
  userId: number;
  deviceInfo: string;
  ipAddress: string;
  country?: string;
  city?: string;
  browser?: string;
  operatingSystem?: string;
  deviceType?: string;
  createdAt: string;
  lastActivity: string;
  expiresAt: string;
  status: string;
  isCurrent?: boolean;
  suspicious?: boolean;
  riskScore?: number;
}

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private apiUrl = `${environment.apiUrl}/sessions`;

  constructor(private http: HttpClient) {}

  getMySessions(): Observable<UserSession[]> {
    return this.http.get<UserSession[]>(`${this.apiUrl}/my-sessions`);
  }

  terminateSession(sessionId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/my-sessions/${sessionId}`);
  }

  terminateAllOtherSessions(): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/my-sessions/others`);
  }
}
