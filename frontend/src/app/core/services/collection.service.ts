import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Collection } from '../models/ebook.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CollectionService {
  private apiUrl = `${environment.apiUrl}/collections`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const userId = localStorage.getItem('userId') || '1';
    return new HttpHeaders({
      'X-User-Id': userId
    });
  }

  createCollection(name: string, description?: string, isPublic: boolean = false): Observable<Collection> {
    return this.http.post<Collection>(
      this.apiUrl,
      { name, description, isPublic },
      { headers: this.getHeaders() }
    );
  }

  updateCollection(
    collectionId: number,
    name?: string,
    description?: string,
    isPublic?: boolean
  ): Observable<Collection> {
    return this.http.put<Collection>(
      `${this.apiUrl}/${collectionId}`,
      { name, description, isPublic },
      { headers: this.getHeaders() }
    );
  }

  deleteCollection(collectionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${collectionId}`, { headers: this.getHeaders() });
  }

  addEbook(collectionId: number, ebookId: number): Observable<Collection> {
    return this.http.post<Collection>(
      `${this.apiUrl}/${collectionId}/ebooks/${ebookId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  removeEbook(collectionId: number, ebookId: number): Observable<Collection> {
    return this.http.delete<Collection>(
      `${this.apiUrl}/${collectionId}/ebooks/${ebookId}`,
      { headers: this.getHeaders() }
    );
  }

  getUserCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${this.apiUrl}/user`, { headers: this.getHeaders() });
  }

  getPublicCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${this.apiUrl}/public`);
  }

  getCollection(collectionId: number): Observable<Collection> {
    return this.http.get<Collection>(`${this.apiUrl}/${collectionId}`);
  }
}
