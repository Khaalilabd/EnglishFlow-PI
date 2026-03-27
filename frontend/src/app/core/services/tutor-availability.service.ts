import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TutorAvailability, TutorStatus } from '../models/tutor-availability.model';

@Injectable({
  providedIn: 'root'
})
export class TutorAvailabilityService {
  private apiUrl = `${environment.apiUrl}/tutor-availability`;

  constructor(private http: HttpClient) {}

  createOrUpdateAvailability(availability: TutorAvailability): Observable<TutorAvailability> {
    return this.http.post<TutorAvailability>(this.apiUrl, availability);
  }

  getById(id: number): Observable<TutorAvailability> {
    return this.http.get<TutorAvailability>(`${this.apiUrl}/${id}`);
  }

  getByTutorId(tutorId: number): Observable<TutorAvailability> {
    return this.http.get<TutorAvailability>(`${this.apiUrl}/tutor/${tutorId}`);
  }

  getAllAvailabilities(): Observable<TutorAvailability[]> {
    return this.http.get<TutorAvailability[]>(this.apiUrl);
  }

  getByStatus(status: TutorStatus): Observable<TutorAvailability[]> {
    return this.http.get<TutorAvailability[]>(`${this.apiUrl}/status/${status}`);
  }

  getAvailableTutors(category: string, level: string): Observable<TutorAvailability[]> {
    const params = new HttpParams()
      .set('category', category)
      .set('level', level);
    return this.http.get<TutorAvailability[]>(`${this.apiUrl}/search`, { params });
  }

  getTutorsWithCapacity(): Observable<TutorAvailability[]> {
    return this.http.get<TutorAvailability[]>(`${this.apiUrl}/with-capacity`);
  }

  deleteAvailability(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
