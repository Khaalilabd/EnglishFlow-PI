import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Member {
  id: number;
  rank: 'PRESIDENT' | 'VICE_PRESIDENT' | 'SECRETARY' | 'TREASURER' | 'COMMUNICATION_MANAGER' | 'EVENT_MANAGER' | 'PARTNERSHIP_MANAGER' | 'MEMBER';
  clubId: number;
  userId: number;
  joinedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  private apiUrl = 'http://localhost:8080/api/members';

  constructor(private http: HttpClient) {}

  getMembersByClub(clubId: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.apiUrl}/club/${clubId}`);
  }

  getMembersByUser(userId: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.apiUrl}/user/${userId}`);
  }

  addMemberToClub(clubId: number, userId: number): Observable<Member> {
    return this.http.post<Member>(`${this.apiUrl}/club/${clubId}/user/${userId}`, {});
  }

  removeMemberFromClub(clubId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/club/${clubId}/user/${userId}`);
  }

  updateMemberRole(memberId: number, role: string, requesterId: number): Observable<Member> {
    console.log('🌐 MemberService.updateMemberRole called:', { memberId, role, requesterId });
    const params = { requesterId: requesterId.toString() };
    console.log('📤 Request params:', params);
    console.log('📤 Request body:', { rank: role });
    return this.http.patch<Member>(`${this.apiUrl}/${memberId}/role`, { rank: role }, { params });
  }

  getClubMemberCount(clubId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/count`);
  }

  isPresident(clubId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/club/${clubId}/user/${userId}/is-president`);
  }

  isMember(clubId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/club/${clubId}/user/${userId}/is-member`);
  }
}
