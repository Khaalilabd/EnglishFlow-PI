import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Lesson, 
  CreateLessonRequest, 
  UpdateLessonRequest,
  LessonType 
} from '../models/lesson.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private apiUrl = `${environment.apiUrl}/lessons`;

  constructor(private http: HttpClient) {}

  // Create a new lesson
  createLesson(lesson: CreateLessonRequest): Observable<Lesson> {
    return this.http.post<Lesson>(this.apiUrl, lesson);
  }

  // Get lesson by ID
  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${this.apiUrl}/${id}`);
  }

  // Get all lessons
  getAllLessons(): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(this.apiUrl);
  }

  // Get lessons by chapter
  getLessonsByChapter(chapterId: number): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(`${this.apiUrl}/chapter/${chapterId}`);
  }

  // Get published lessons by chapter
  getPublishedLessonsByChapter(chapterId: number): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(`${this.apiUrl}/chapter/${chapterId}/published`);
  }

  // Get lessons by type
  getLessonsByType(lessonType: LessonType): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(`${this.apiUrl}/type/${lessonType}`);
  }

  // Get preview lessons
  getPreviewLessons(): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(`${this.apiUrl}/preview`);
  }

  // Update lesson
  updateLesson(id: number, lesson: UpdateLessonRequest): Observable<Lesson> {
    return this.http.put<Lesson>(`${this.apiUrl}/${id}`, lesson);
  }

  // Delete lesson
  deleteLesson(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
