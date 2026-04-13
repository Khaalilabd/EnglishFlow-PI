import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../core/services/auth.service';

export interface VocabularyWord {
  id: number;
  word: string;
  definition: string;
  phonetic?: string;
  partOfSpeech?: string;
  example?: string;
  synonyms?: string;
  antonyms?: string;
  audioUrl?: string;
  sourceTopicId?: number;
  masteryLevel: 'NEW' | 'LEARNING' | 'FAMILIAR' | 'MASTERED';
  reviewCount: number;
  lastReviewedAt?: string;
  createdAt: string;
}

export interface SaveVocabularyRequest {
  word: string;
  definition: string;
  phonetic?: string;
  partOfSpeech?: string;
  example?: string;
  synonyms?: string;
  antonyms?: string;
  audioUrl?: string;
  sourceTopicId?: number;
}

export interface VocabularyStats {
  totalWords: number;
  newWords: number;
  learningWords: number;
  familiarWords: number;
  masteredWords: number;
  totalReviews: number;
}

export interface VocabularyPage {
  content: VocabularyWord[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class VocabularyService {
  private apiUrl = `${environment.apiUrl}/community/vocabulary`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const userId = this.authService.currentUserValue?.id;
    return userId ? new HttpHeaders({ 'X-User-Id': userId.toString() }) : new HttpHeaders();
  }

  saveWord(request: SaveVocabularyRequest): Observable<VocabularyWord> {
    return this.http.post<VocabularyWord>(this.apiUrl, request, { headers: this.getHeaders() });
  }

  getUserVocabulary(page: number = 0, size: number = 20, sortBy: string = 'createdAt', level?: string): Observable<VocabularyPage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    if (level && level !== 'all') {
      params = params.set('level', level);
    }
    
    return this.http.get<VocabularyPage>(this.apiUrl, { params, headers: this.getHeaders() });
  }

  searchVocabulary(query: string, page: number = 0, size: number = 20): Observable<VocabularyPage> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<VocabularyPage>(`${this.apiUrl}/search`, { params, headers: this.getHeaders() });
  }

  getStats(): Observable<VocabularyStats> {
    return this.http.get<VocabularyStats>(`${this.apiUrl}/stats`, { headers: this.getHeaders() });
  }

  markAsReviewed(wordId: number): Observable<VocabularyWord> {
    return this.http.put<VocabularyWord>(`${this.apiUrl}/${wordId}/review`, {}, { headers: this.getHeaders() });
  }

  deleteWord(wordId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${wordId}`, { headers: this.getHeaders() });
  }

  isWordSaved(word: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check/${word}`, { headers: this.getHeaders() });
  }

  exportVocabulary(): Observable<VocabularyWord[]> {
    return this.http.get<VocabularyWord[]>(`${this.apiUrl}/export`, { headers: this.getHeaders() });
  }
}
