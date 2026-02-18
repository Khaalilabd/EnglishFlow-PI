import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Chapter, 
  CreateChapterRequest, 
  UpdateChapterRequest 
} from '../models/chapter.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChapterService {
  private apiUrl = `${environment.apiUrl}/chapters`;

  constructor(private http: HttpClient) {}

  // Create a new chapter
  createChapter(chapter: CreateChapterRequest): Observable<Chapter> {
    return this.http.post<Chapter>(this.apiUrl, chapter);
  }

  // Get chapter by ID
  getChapterById(id: number): Observable<Chapter> {
    return this.http.get<Chapter>(`${this.apiUrl}/${id}`);
  }

  // Get all chapters
  getAllChapters(): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(this.apiUrl);
  }

  // Get chapters by course
  getChaptersByCourse(courseId: number): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(`${this.apiUrl}/course/${courseId}`);
  }

  // Get published chapters by course
  getPublishedChaptersByCourse(courseId: number): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(`${this.apiUrl}/course/${courseId}/published`);
  }

  // Update chapter
  updateChapter(id: number, chapter: UpdateChapterRequest): Observable<Chapter> {
    return this.http.put<Chapter>(`${this.apiUrl}/${id}`, chapter);
  }

  // Delete chapter
  deleteChapter(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
