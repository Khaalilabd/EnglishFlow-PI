import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../core/services/auth.service';
import { environment } from '../../environments/environment';
import { catchError, of } from 'rxjs';

/**
 * Service pour tracker automatiquement l'activité des étudiants
 */
@Injectable({
  providedIn: 'root'
})
export class ActivityTrackerService {
  private apiUrl = environment.apiUrl;
  private clickCount = 0;
  private sessionStarted = false;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.initializeTracking();
  }

  /**
   * Initialise le tracking automatique
   */
  private initializeTracking(): void {
    // Track les clics globalement
    document.addEventListener('click', () => this.trackClick());
    
    // Track la session au démarrage
    const user = this.authService.currentUserValue;
    if (user && !this.sessionStarted) {
      this.trackSession();
      this.sessionStarted = true;
    }

    // Track les clics toutes les 10 clics
    setInterval(() => {
      if (this.clickCount >= 10) {
        this.sendClicksToServer();
      }
    }, 5000); // Vérifier toutes les 5 secondes
  }

  /**
   * Track un clic
   */
  private trackClick(): void {
    const user = this.authService.currentUserValue;
    if (user && user.role === 'STUDENT') {
      this.clickCount++;
    }
  }

  /**
   * Envoie les clics au serveur
   */
  private sendClicksToServer(): void {
    if (this.clickCount === 0) return;

    const user = this.authService.currentUserValue;
    if (!user) return;

    const clicks = this.clickCount;
    this.clickCount = 0; // Reset

    this.http.post(`${this.apiUrl}/analytics/student/${user.id}/click`, { clicks })
      .pipe(
        catchError(error => {
          console.error('Erreur tracking clicks:', error);
          return of(null);
        })
      )
      .subscribe();
  }

  /**
   * Track une session (appelé au login)
   */
  trackSession(): void {
    const user = this.authService.currentUserValue;
    if (!user || user.role !== 'STUDENT') return;

    console.log('📊 Tracking session pour user:', user.id);

    this.http.post(`${this.apiUrl}/analytics/student/${user.id}/session`, {})
      .pipe(
        catchError(error => {
          console.error('Erreur tracking session:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        console.log('✅ Session trackée');
      });
  }

  /**
   * Track une évaluation (quiz/exam)
   */
  trackAssessment(score: number, type: 'TMA' | 'CMA' | 'EXAM' = 'TMA'): void {
    const user = this.authService.currentUserValue;
    if (!user || user.role !== 'STUDENT') return;

    console.log(`📊 Tracking assessment: score=${score}, type=${type}`);

    this.http.post(`${this.apiUrl}/analytics/student/${user.id}/assessment`, { score, type })
      .pipe(
        catchError(error => {
          console.error('Erreur tracking assessment:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        console.log('✅ Assessment tracké');
      });
  }

  /**
   * Ajoute des crédits étudiés
   */
  addStudiedCredits(credits: number): void {
    const user = this.authService.currentUserValue;
    if (!user || user.role !== 'STUDENT') return;

    console.log(`📊 Ajout de ${credits} crédits`);

    this.http.post(`${this.apiUrl}/analytics/student/${user.id}/credits`, { credits })
      .pipe(
        catchError(error => {
          console.error('Erreur ajout crédits:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        console.log('✅ Crédits ajoutés');
      });
  }

  /**
   * Incrémente les tentatives (échec quiz)
   */
  incrementAttempts(): void {
    const user = this.authService.currentUserValue;
    if (!user || user.role !== 'STUDENT') return;

    console.log('📊 Incrémentation des tentatives');

    this.http.post(`${this.apiUrl}/analytics/student/${user.id}/attempt`, {})
      .pipe(
        catchError(error => {
          console.error('Erreur incrémentation tentatives:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        console.log('✅ Tentatives incrémentées');
      });
  }

  /**
   * Force l'envoi des clics en attente (appelé avant de quitter)
   */
  flush(): void {
    this.sendClicksToServer();
  }
}
