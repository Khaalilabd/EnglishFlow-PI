import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quiz, Question, QuizAttempt, AttemptRequest, AttemptResult } from '../models/quiz.model';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = `${environment.apiUrl}/learning`; // Via API Gateway

  constructor(private http: HttpClient) {}

  // Quiz endpoints
  getAllQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/quizzes`);
  }

  getPublishedQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/quizzes/published`);
  }

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/quizzes/${id}`);
  }

  createQuiz(quiz: Quiz): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.apiUrl}/quizzes`, quiz);
  }

  updateQuiz(id: number, quiz: Quiz): Observable<Quiz> {
    return this.http.put<Quiz>(`${this.apiUrl}/quizzes/${id}`, quiz);
  }

  deleteQuiz(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/quizzes/${id}`);
  }

  // Question endpoints
  getQuestionsByQuizId(quizId: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/questions/quiz/${quizId}`);
  }

  createQuestion(question: Question): Observable<Question> {
    return this.http.post<Question>(`${this.apiUrl}/questions`, question);
  }

  updateQuestion(id: number, question: Question): Observable<Question> {
    return this.http.put<Question>(`${this.apiUrl}/questions/${id}`, question);
  }

  deleteQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/questions/${id}`);
  }

  // Quiz attempt endpoints
  startAttempt(quizId: number, studentId: number): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(`${this.apiUrl}/attempts/start?quizId=${quizId}&studentId=${studentId}`, {});
  }

  submitAttempt(attemptId: number, request: AttemptRequest): Observable<AttemptResult> {
    return this.http.post<AttemptResult>(`${this.apiUrl}/attempts/${attemptId}/submit`, request);
  }

  getStudentAttempts(studentId: number): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(`${this.apiUrl}/attempts/student/${studentId}`);
  }

  getAttemptResult(attemptId: number): Observable<AttemptResult> {
    return this.http.get<AttemptResult>(`${this.apiUrl}/attempts/${attemptId}/result`);
  }
}
