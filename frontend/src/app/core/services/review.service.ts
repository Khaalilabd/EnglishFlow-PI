import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review, CreateReviewRequest } from '../models/ebook.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/learning/ebooks`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const userId = localStorage.getItem('userId') || '1';
    return new HttpHeaders({
      'X-User-Id': userId
    });
  }

  createReview(request: CreateReviewRequest): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/${request.ebookId}/reviews`, request, { headers: this.getHeaders() });
  }

  updateReview(ebookId: number, reviewId: number, request: CreateReviewRequest): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${ebookId}/reviews/${reviewId}`, request, { headers: this.getHeaders() });
  }

  deleteReview(ebookId: number, reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${ebookId}/reviews/${reviewId}`, { headers: this.getHeaders() });
  }

  getEbookReviews(ebookId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/${ebookId}/reviews`);
  }

  getUserReviews(userId: number): Observable<Review[]> {
    // This endpoint is not available in the new structure, we'll need to add it later
    return this.http.get<Review[]>(`${this.apiUrl}/user/${userId}/reviews`);
  }

  markHelpful(ebookId: number, reviewId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${ebookId}/reviews/${reviewId}/helpful`, {});
  }
}
