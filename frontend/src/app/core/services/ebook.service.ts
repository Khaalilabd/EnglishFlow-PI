import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ebook } from '../models/ebook.model';

@Injectable({
  providedIn: 'root'
})
export class EbookService {
  private apiUrl = `${environment.apiUrl}/learning/ebooks`; // Via API Gateway

  constructor(private http: HttpClient) {}

  getAllEbooks(): Observable<Ebook[]> {
    return this.http.get<Ebook[]>(this.apiUrl);
  }

  getFreeEbooks(): Observable<Ebook[]> {
    return this.http.get<Ebook[]>(`${this.apiUrl}/free`);
  }

  getEbooksByLevel(level: string): Observable<Ebook[]> {
    return this.http.get<Ebook[]>(`${this.apiUrl}/level/${level}`);
  }

  getEbookById(id: number): Observable<Ebook> {
    return this.http.get<Ebook>(`${this.apiUrl}/${id}`);
  }

  createEbook(ebook: Ebook, file?: File, coverImage?: File): Observable<Ebook> {
    const formData = new FormData();
    formData.append('ebook', new Blob([JSON.stringify(ebook)], { type: 'application/json' }));
    if (file) {
      formData.append('file', file);
    }
    if (coverImage) {
      formData.append('coverImage', coverImage);
    }
    return this.http.post<Ebook>(this.apiUrl, formData);
  }

  updateEbook(id: number, ebook: Ebook, file?: File, coverImage?: File): Observable<Ebook> {
    const formData = new FormData();
    formData.append('ebook', new Blob([JSON.stringify(ebook)], { type: 'application/json' }));
    if (file) {
      formData.append('file', file);
    }
    if (coverImage) {
      formData.append('coverImage', coverImage);
    }
    return this.http.put<Ebook>(`${this.apiUrl}/${id}`, formData);
  }

  deleteEbook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  trackAccess(ebookId: number, studentId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${ebookId}/access?studentId=${studentId}`, {});
  }

  downloadEbook(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/download`, {
      responseType: 'blob'
    });
  }
}
