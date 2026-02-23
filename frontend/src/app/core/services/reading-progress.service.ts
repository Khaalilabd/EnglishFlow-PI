import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReadingProgress, UpdateProgressRequest } from '../models/ebook.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReadingProgressService {
  private apiUrl = `${environment.apiUrl}/reading-progress`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const userId = localStorage.getItem('userId') || '1';
    return new HttpHeaders({
      'X-User-Id': userId
    });
  }

  updateProgress(request: UpdateProgressRequest): Observable<ReadingProgress> {
    return this.http.post<ReadingProgress>(this.apiUrl, request, { headers: this.getHeaders() });
  }

  getProgress(ebookId: number): Observable<ReadingProgress> {
    return this.http.get<ReadingProgress>(`${this.apiUrl}/ebook/${ebookId}`, { headers: this.getHeaders() });
  }

  getUserProgress(): Observable<ReadingProgress[]> {
    return this.http.get<ReadingProgress[]>(`${this.apiUrl}/user`, { headers: this.getHeaders() });
  }

  getInProgressBooks(): Observable<ReadingProgress[]> {
    return this.http.get<ReadingProgress[]>(`${this.apiUrl}/user/in-progress`, { headers: this.getHeaders() });
  }

  getCompletedBooks(): Observable<ReadingProgress[]> {
    return this.http.get<ReadingProgress[]>(`${this.apiUrl}/user/completed`, { headers: this.getHeaders() });
  }
}
