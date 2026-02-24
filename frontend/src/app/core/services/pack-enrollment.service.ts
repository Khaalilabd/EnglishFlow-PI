import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PackEnrollment } from '../models/pack-enrollment.model';

@Injectable({
  providedIn: 'root'
})
export class PackEnrollmentService {
  private apiUrl = `${environment.apiUrl}/pack-enrollments`;

  constructor(private http: HttpClient) {}

  enrollStudent(studentId: number, packId: number): Observable<PackEnrollment> {
    const params = new HttpParams()
      .set('studentId', studentId.toString())
      .set('packId', packId.toString());
    return this.http.post<PackEnrollment>(this.apiUrl, null, { params });
  }

  getById(id: number): Observable<PackEnrollment> {
    return this.http.get<PackEnrollment>(`${this.apiUrl}/${id}`);
  }

  getByStudentId(studentId: number): Observable<PackEnrollment[]> {
    return this.http.get<PackEnrollment[]>(`${this.apiUrl}/student/${studentId}`);
  }

  getActiveEnrollmentsByStudent(studentId: number): Observable<PackEnrollment[]> {
    return this.http.get<PackEnrollment[]>(`${this.apiUrl}/student/${studentId}/active`);
  }

  getByPackId(packId: number): Observable<PackEnrollment[]> {
    return this.http.get<PackEnrollment[]>(`${this.apiUrl}/pack/${packId}`);
  }

  getByTutorId(tutorId: number): Observable<PackEnrollment[]> {
    return this.http.get<PackEnrollment[]>(`${this.apiUrl}/tutor/${tutorId}`);
  }

  updateProgress(enrollmentId: number, progressPercentage: number): Observable<PackEnrollment> {
    const params = new HttpParams().set('progressPercentage', progressPercentage.toString());
    return this.http.put<PackEnrollment>(`${this.apiUrl}/${enrollmentId}/progress`, null, { params });
  }

  completeEnrollment(enrollmentId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${enrollmentId}/complete`, null);
  }

  cancelEnrollment(enrollmentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${enrollmentId}`);
  }

  isStudentEnrolled(studentId: number, packId: number): Observable<boolean> {
    const params = new HttpParams()
      .set('studentId', studentId.toString())
      .set('packId', packId.toString());
    return this.http.get<boolean>(`${this.apiUrl}/check`, { params });
  }
}
