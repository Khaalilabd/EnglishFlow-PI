import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { GamificationService } from './gamification.service';
import { QuizService } from '../core/services/quiz.service';

export interface StudentAnalyticsData {
  // Données pour le ML
  previousAttempts: number;
  credits: number;
  totalClicks: number;
  sessions: number;
  avgClicks: number;
  maxClicks: number;
  avgScore: number;
  minScore: number;
  maxScore: number;
  assessments: number;
  registrationDate: number;
  isUnregistered: number;
  
  // Métadonnées
  dataQuality: 'high' | 'medium' | 'low';
  lastUpdated: Date;
}

@Injectable({
  providedIn: 'root'
})
export class StudentAnalyticsService {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private gamificationService: GamificationService,
    private quizService: QuizService
  ) {}

  /**
   * Récupère et agrège toutes les données d'un étudiant pour le ML
   * Utilise maintenant la nouvelle API StudentAnalytics pour les vraies données
   */
  getStudentAnalytics(studentId: number): Observable<StudentAnalyticsData> {
    // Essayer d'abord de récupérer les vraies analytics depuis la BD
    return this.http.get<any>(`${this.apiUrl}/analytics/student/${studentId}`).pipe(
      map(analytics => this.convertToAnalyticsData(analytics)),
      catchError(error => {
        console.warn('Analytics API non disponible', error);
        // Retourner des données par défaut
        return of(this.getDefaultAnalytics());
      })
    );
  }

  /**
   * Convertit les analytics de la BD vers le format ML
   */
  private convertToAnalyticsData(analytics: any): StudentAnalyticsData {
    // Calculer les jours depuis l'inscription
    const registrationDate = analytics.firstRegistrationDate
      ? -Math.floor((Date.now() - new Date(analytics.firstRegistrationDate).getTime()) / (1000 * 60 * 60 * 24))
      : -30;

    return {
      previousAttempts: analytics.previousAttempts || 0,
      credits: analytics.studiedCredits || 0,
      totalClicks: analytics.totalClicks || 0,
      sessions: analytics.totalSessions || 0,
      avgClicks: analytics.avgClicksPerSession || 0,
      maxClicks: analytics.maxClicksInSession || 0,
      avgScore: analytics.avgScore || 0,
      minScore: analytics.minScore || 0,
      maxScore: analytics.maxScore || 0,
      assessments: analytics.totalAssessments || 0,
      registrationDate: registrationDate,
      isUnregistered: analytics.isUnregistered ? 1 : 0,
      dataQuality: this.assessRealDataQuality(analytics),
      lastUpdated: new Date()
    };
  }

  /**
   * Évalue la qualité des vraies données
   */
  private assessRealDataQuality(analytics: any): 'high' | 'medium' | 'low' {
    const hasClicks = analytics.totalClicks > 0;
    const hasSessions = analytics.totalSessions > 0;
    const hasAssessments = analytics.totalAssessments > 0;
    const hasScores = analytics.avgScore > 0;

    const score = [hasClicks, hasSessions, hasAssessments, hasScores].filter(Boolean).length;

    if (score >= 3) return 'high';
    if (score >= 2) return 'medium';
    return 'low';
  }

  /**
   * Retourne des analytics par défaut
   */
  private getDefaultAnalytics(): StudentAnalyticsData {
    return {
      previousAttempts: 0,
      credits: 0,
      totalClicks: 0,
      sessions: 0,
      avgClicks: 0,
      maxClicks: 0,
      avgScore: 0,
      minScore: 0,
      maxScore: 0,
      assessments: 0,
      registrationDate: -30,
      isUnregistered: 0,
      dataQuality: 'low',
      lastUpdated: new Date()
    };
  }

  // ========== MÉTHODES DE TRACKING EN TEMPS RÉEL ==========

  /**
   * Track un clic de l'étudiant
   */
  trackClick(userId: number, clicks: number = 1): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/click`, { clicks })
      .pipe(
        catchError(error => {
          console.error('Erreur lors du tracking du clic:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Track une session de l'étudiant (à appeler au login)
   */
  trackSession(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/session`, {})
      .pipe(
        catchError(error => {
          console.error('Erreur lors du tracking de la session:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Track une évaluation complétée
   */
  trackAssessment(userId: number, score: number, type: 'TMA' | 'CMA' | 'EXAM'): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/assessment`, { score, type })
      .pipe(
        catchError(error => {
          console.error('Erreur lors du tracking de l\'évaluation:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Ajoute des crédits étudiés (à appeler quand un cours est complété)
   */
  addStudiedCredits(userId: number, credits: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/credits`, { credits })
      .pipe(
        catchError(error => {
          console.error('Erreur lors de l\'ajout des crédits:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Incrémente les tentatives (à appeler quand un étudiant échoue un quiz)
   */
  incrementAttempts(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/attempt`, {})
      .pipe(
        catchError(error => {
          console.error('Erreur lors de l\'incrémentation des tentatives:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Track l'ouverture d'une leçon
   */
  trackLessonOpened(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/lesson-opened`, {})
      .pipe(
        catchError(error => {
          console.error('Error tracking lesson opened:', error);
          return of(void 0);
        })
      );
  }

  /**
   * Ajoute du temps passé sur une leçon (en minutes)
   */
  addTimeSpent(userId: number, minutes: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analytics/student/${userId}/time-spent`, { minutes })
      .pipe(
        catchError(error => {
          console.error('Error adding time spent:', error);
          return of(void 0);
        })
      );
  }
}
