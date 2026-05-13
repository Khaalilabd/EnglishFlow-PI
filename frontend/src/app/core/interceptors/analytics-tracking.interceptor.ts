import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { StudentAnalyticsService } from '../../services/student-analytics.service';

/**
 * Intercepteur pour tracker automatiquement les interactions des étudiants
 */
@Injectable()
export class AnalyticsTrackingInterceptor implements HttpInterceptor {
  
  private clickCount = 0;
  private lastTrackTime = Date.now();
  private readonly TRACK_INTERVAL = 30000; // Track toutes les 30 secondes

  constructor(
    private authService: AuthService,
    private analyticsService: StudentAnalyticsService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Incrémenter le compteur de clics pour chaque requête
    const currentUser = this.authService.currentUserValue;
    
    if (currentUser && currentUser.role === 'STUDENT') {
      this.clickCount++;
      
      // Track les clics par batch toutes les 30 secondes
      const now = Date.now();
      if (now - this.lastTrackTime >= this.TRACK_INTERVAL && this.clickCount > 0) {
        this.analyticsService.trackClick(currentUser.id, this.clickCount).subscribe();
        this.clickCount = 0;
        this.lastTrackTime = now;
      }
    }

    return next.handle(req);
  }
}
