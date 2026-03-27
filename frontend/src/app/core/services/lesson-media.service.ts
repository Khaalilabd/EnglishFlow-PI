import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LessonMedia } from '../models/lesson-media.model';

@Injectable({
  providedIn: 'root'
})
export class LessonMediaService {
  private apiUrl = `${environment.apiUrl}/lesson-media`;

  constructor(private http: HttpClient) {}

  createMedia(media: LessonMedia): Observable<LessonMedia> {
    return this.http.post<LessonMedia>(this.apiUrl, media);
  }

  updateMedia(id: number, media: LessonMedia): Observable<LessonMedia> {
    return this.http.put<LessonMedia>(`${this.apiUrl}/${id}`, media);
  }

  deleteMedia(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getMediaById(id: number): Observable<LessonMedia> {
    return this.http.get<LessonMedia>(`${this.apiUrl}/${id}`);
  }

  getMediaByLesson(lessonId: number): Observable<LessonMedia[]> {
    return this.http.get<LessonMedia[]>(`${this.apiUrl}/lesson/${lessonId}`);
  }

  reorderMedia(lessonId: number, mediaIds: number[]): Observable<LessonMedia[]> {
    return this.http.put<LessonMedia[]>(`${this.apiUrl}/lesson/${lessonId}/reorder`, { mediaIds });
  }
}
