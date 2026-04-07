import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Skill } from '../models/club.model';

@Injectable({
  providedIn: 'root'
})
export class SkillService {
  private apiUrl = `${environment.apiUrl}/clubs`;

  constructor(private http: HttpClient) {}

  getSkillsByClub(clubId: number): Observable<Skill[]> {
    return this.http.get<Skill[]>(`${this.apiUrl}/${clubId}/skills`);
  }

  addSkillToClub(clubId: number, skill: Skill): Observable<Skill> {
    return this.http.post<Skill>(`${this.apiUrl}/${clubId}/skills`, skill);
  }

  updateClubSkills(clubId: number, skills: Skill[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${clubId}/skills`, skills);
  }

  deleteSkill(clubId: number, skillId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${clubId}/skills/${skillId}`);
  }
}
