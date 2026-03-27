import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../models/ebook.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private apiUrl = `${environment.apiUrl}/notes`;

  constructor(private http: HttpClient) {}

  createNote(
    progressId: number,
    pageNumber: number,
    content: string,
    highlightedText?: string,
    color?: string
  ): Observable<Note> {
    return this.http.post<Note>(this.apiUrl, {
      progressId,
      pageNumber,
      content,
      highlightedText,
      color
    });
  }

  updateNote(noteId: number, content: string): Observable<Note> {
    return this.http.put<Note>(`${this.apiUrl}/${noteId}`, { content });
  }

  deleteNote(noteId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${noteId}`);
  }

  getProgressNotes(progressId: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/progress/${progressId}`);
  }

  getPageNotes(progressId: number, pageNumber: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/progress/${progressId}/page/${pageNumber}`);
  }
}
