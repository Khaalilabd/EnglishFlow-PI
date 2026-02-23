import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export enum ReactionType {
  LIKE = 'LIKE',
  HELPFUL = 'HELPFUL',
  INSIGHTFUL = 'INSIGHTFUL'
}

export interface ReactionCount {
  type: ReactionType;
  count: number;
}

export interface ReactionRequest {
  type: ReactionType;
}

@Injectable({
  providedIn: 'root'
})
export class ReactionService {
  private apiUrl = `${environment.apiUrl}/community/reactions`;

  constructor(private http: HttpClient) {}

  // Topic reactions
  addReactionToTopic(topicId: number, type: ReactionType): Observable<void> {
    const request: ReactionRequest = { type };
    return this.http.post<void>(`${this.apiUrl}/topic/${topicId}`, request);
  }

  removeReactionFromTopic(topicId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/topic/${topicId}`);
  }

  getTopicReactions(topicId: number): Observable<ReactionCount[]> {
    return this.http.get<ReactionCount[]>(`${this.apiUrl}/topic/${topicId}/count`);
  }

  // Post reactions
  addReactionToPost(postId: number, type: ReactionType): Observable<void> {
    const request: ReactionRequest = { type };
    return this.http.post<void>(`${this.apiUrl}/post/${postId}`, request);
  }

  removeReactionFromPost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/post/${postId}`);
  }

  getPostReactions(postId: number): Observable<ReactionCount[]> {
    return this.http.get<ReactionCount[]>(`${this.apiUrl}/post/${postId}/count`);
  }
}
