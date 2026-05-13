import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface PredictionRequest {
  num_of_prev_attempts: number;
  studied_credits: number;
  total_clicks: number;
  nb_sessions: number;
  avg_clicks: number;
  max_clicks: number;
  avg_score: number;
  min_score: number;
  max_score: number;
  nb_assessments: number;
  date_registration: number;
  is_unregistered: number;
}

export interface PredictionResponse {
  student_id?: string;
  prediction: number;
  prediction_label: string;
  probability: {
    echec: number;
    succes: number;
  };
  confidence: number;
  risk_level: string;
  recommendations?: string[];
}

export interface ClusteringResponse {
  student_id?: string;
  cluster: number;
  cluster_label: string;
  characteristics: any;
  recommendations: string[];
}

export interface CourseRecommendation {
  course_code: string;
  course_name?: string;
  recommendation_score: number;
  avg_interaction: number;
  nb_students: number;
  success_rate: number;
  reason?: string;
}

export interface RecommendationListResponse {
  student_id?: string;
  recommendations: CourseRecommendation[];
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class MlService {
  private apiUrl = environment.mlServiceUrl;

  constructor(private http: HttpClient) {}

  /**
   * Prédit le succès d'un étudiant
   */
  predictStudentSuccess(data: PredictionRequest): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(`${this.apiUrl}/prediction/student`, data)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la prédiction:', error);
          return of({
            prediction: 0,
            prediction_label: 'Erreur',
            probability: { echec: 0.5, succes: 0.5 },
            confidence: 0,
            risk_level: 'unknown',
            recommendations: ['Service ML temporairement indisponible']
          });
        })
      );
  }

  /**
   * Identifie le cluster d'un étudiant
   */
  identifyStudentCluster(data: any): Observable<ClusteringResponse> {
    return this.http.post<ClusteringResponse>(`${this.apiUrl}/clustering/student`, data)
      .pipe(
        catchError(error => {
          console.error('Erreur lors du clustering:', error);
          return of({
            cluster: 0,
            cluster_label: 'Non disponible',
            characteristics: {},
            recommendations: []
          });
        })
      );
  }

  /**
   * Recommande des cours pour un étudiant
   */
  recommendCourses(studentId: string, limit: number = 5): Observable<RecommendationListResponse> {
    const params = new HttpParams()
      .set('limit', limit.toString());

    return this.http.get<RecommendationListResponse>(
      `${this.apiUrl}/recommendation/courses/${studentId}`,
      { params }
    ).pipe(
      catchError(error => {
        console.error('Erreur lors de la recommandation:', error);
        return of({
          student_id: studentId,
          recommendations: [],
          count: 0
        });
      })
    );
  }

  /**
   * Recommande des cours pour un nouvel étudiant
   */
  recommendCoursesForNewStudent(avgScore: number, limit: number = 5): Observable<RecommendationListResponse> {
    const data = {
      avg_score: avgScore,
      n_recommendations: limit
    };

    return this.http.post<RecommendationListResponse>(
      `${this.apiUrl}/recommendation/courses/new-student`,
      data
    ).pipe(
      catchError(error => {
        console.error('Erreur lors de la recommandation:', error);
        return of({
          recommendations: [],
          count: 0
        });
      })
    );
  }

  /**
   * Vérifie l'état du service ML
   */
  healthCheck(): Observable<any> {
    return this.http.get(`${this.apiUrl.replace('/api/ml', '')}/health`)
      .pipe(
        catchError(error => {
          console.error('Service ML indisponible:', error);
          return of({ status: 'unavailable' });
        })
      );
  }

  /**
   * Récupère les analytics d'un étudiant depuis la BD via le gateway
   */
  getStudentAnalytics(studentId: number): Observable<any> {
    // Utiliser environment.apiUrl (gateway) au lieu de mlServiceUrl
    return this.http.get<any>(`${environment.apiUrl}/analytics/student/${studentId}`)
      .pipe(
        catchError(error => {
          console.error('Erreur récupération analytics:', error);
          throw error;
        })
      );
  }

  /**
   * Calcule les données de prédiction à partir du profil étudiant
   * Accepte maintenant StudentAnalyticsData ou l'ancien format
   */
  calculatePredictionData(studentProfile: any): PredictionRequest {
    // Si c'est déjà au bon format (StudentAnalyticsData)
    if (studentProfile.previousAttempts !== undefined) {
      return {
        num_of_prev_attempts: studentProfile.previousAttempts || 0,
        studied_credits: studentProfile.credits || 0,
        total_clicks: studentProfile.totalClicks || 0,
        nb_sessions: studentProfile.sessions || 0,
        avg_clicks: studentProfile.avgClicks || 0,
        max_clicks: studentProfile.maxClicks || 0,
        avg_score: studentProfile.avgScore || 0,
        min_score: studentProfile.minScore || 0,
        max_score: studentProfile.maxScore || 0,
        nb_assessments: studentProfile.assessments || 0,
        date_registration: studentProfile.registrationDate || 0,
        is_unregistered: studentProfile.isUnregistered || 0
      };
    }
    
    // Ancien format (fallback)
    return {
      num_of_prev_attempts: studentProfile.previousAttempts || 0,
      studied_credits: studentProfile.credits || 0,
      total_clicks: studentProfile.totalClicks || 0,
      nb_sessions: studentProfile.sessions || 0,
      avg_clicks: studentProfile.avgClicks || 0,
      max_clicks: studentProfile.maxClicks || 0,
      avg_score: studentProfile.avgScore || 0,
      min_score: studentProfile.minScore || 0,
      max_score: studentProfile.maxScore || 0,
      nb_assessments: studentProfile.assessments || 0,
      date_registration: studentProfile.registrationDate || 0,
      is_unregistered: studentProfile.isUnregistered || 0
    };
  }
}
