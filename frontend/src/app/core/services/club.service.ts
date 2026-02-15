import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Club, CreateClubRequest, UpdateClubRequest, Member, JoinClubRequest, ClubCategory } from '../models/club.model';

@Injectable({
  providedIn: 'root'
})
export class ClubService {
  private apiUrl = 'http://localhost:8084/api/clubs';

  constructor(private http: HttpClient) {}

  getAllClubs(): Observable<Club[]> {
    return this.http.get<Club[]>(this.apiUrl);
  }

  getClubById(id: number): Observable<Club> {
    return this.http.get<Club>(`${this.apiUrl}/${id}`);
  }

  getClubsByCategory(category: ClubCategory): Observable<Club[]> {
    return this.http.get<Club[]>(`${this.apiUrl}/category/${category}`);
  }

  getAvailableClubs(): Observable<Club[]> {
    return this.http.get<Club[]>(`${this.apiUrl}/available`);
  }

  createClub(club: CreateClubRequest): Observable<Club> {
    return this.http.post<Club>(this.apiUrl, club);
  }

  updateClub(id: number, club: UpdateClubRequest): Observable<Club> {
    return this.http.put<Club>(`${this.apiUrl}/${id}`, club);
  }

  deleteClub(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getClubMembers(clubId: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.apiUrl}/${clubId}/members`);
  }

  joinClub(clubId: number, request: JoinClubRequest): Observable<Member> {
    return this.http.post<Member>(`${this.apiUrl}/${clubId}/join`, request);
  }

  leaveClub(clubId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${clubId}/leave/${userId}`);
  }

  updateMemberRank(clubId: number, memberId: number, rank: string): Observable<Member> {
    return this.http.patch<Member>(`${this.apiUrl}/${clubId}/members/${memberId}/rank`, { rank });
  }
}
