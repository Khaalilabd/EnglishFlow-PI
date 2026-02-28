import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface QuizAttempt {
  id?: number;
  studentId: number;
  quizId: number;
  score: number;
  answers: { [questionId: number]: any };
  completedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class QuizAttemptService {
  private apiUrl = `${environment.apiUrl}/quiz-attempts`;

  constructor(private http: HttpClient) {}

  submitAttempt(attempt: Partial<QuizAttempt>): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(this.apiUrl, attempt);
  }

  getAttemptsByStudent(studentId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(`${this.apiUrl}/student/${studentId}`);
  }

  getAttemptsByQuiz(quizId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(`${this.apiUrl}/quiz/${quizId}`);
  }

  getAttemptById(id: number): Observable<QuizAttempt> {
    return this.http.get<QuizAttempt>(`${this.apiUrl}/${id}`);
  }
}
