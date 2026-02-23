import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bookmark } from '../models/ebook.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookmarkService {
  private apiUrl = `${environment.apiUrl}/bookmarks`;

  constructor(private http: HttpClient) {}

  createBookmark(progressId: number, pageNumber: number, note?: string): Observable<Bookmark> {
    return this.http.post<Bookmark>(this.apiUrl, { progressId, pageNumber, note });
  }

  deleteBookmark(bookmarkId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${bookmarkId}`);
  }

  getProgressBookmarks(progressId: number): Observable<Bookmark[]> {
    return this.http.get<Bookmark[]>(`${this.apiUrl}/progress/${progressId}`);
  }
}
