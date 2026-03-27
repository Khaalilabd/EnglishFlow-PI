
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chapter, CreateChapterRequest, UpdateChapterRequest } from '../models/chapter.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChapterService {
  private apiUrl = `${environment.apiUrl}/chapters`;

  constructor(private http: HttpClient) {}

  createChapter(chapter: CreateChapterRequest): Observable<Chapter> {
    return this.http.post<Chapter>(this.apiUrl, chapter);
  }

  getChapterById(id: number): Observable<Chapter> {
    return this.http.get<Chapter>(`${this.apiUrl}/${id}`);
  }

  getAllChapters(): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(this.apiUrl);
  }

  getChaptersByCourse(courseId: number): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(`${this.apiUrl}/course/${courseId}`);
  }

  getPublishedChaptersByCourse(courseId: number): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(`${this.apiUrl}/course/${courseId}/published`);
  }

  updateChapter(id: number, chapter: UpdateChapterRequest): Observable<Chapter> {
    return this.http.put<Chapter>(`${this.apiUrl}/${id}`, chapter);
  }

  deleteChapter(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  chapterExists(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${id}/exists`);
  }

  chapterBelongsToCourse(chapterId: number, courseId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${chapterId}/belongs-to-course/${courseId}`);
  }
}
