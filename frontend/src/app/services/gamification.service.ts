import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Badge {
  id: number;
  code: string;
  name: string;
  description: string;
  icon: string;
  type: string;
  rarity: string;
  rarityIcon: string;
  rarityColor: string;
  coinsReward: number;
  isEarned: boolean;
  isDisplayed: boolean;
  isNew: boolean;
  earnedAt?: string;
}

export interface UserLevel {
  userId: number;
  assessmentLevel: string | null;
  assessmentLevelIcon: string;
  assessmentLevelName: string;
  hasCompletedAssessment: boolean;
  assessmentCompletedAt?: string;
  certifiedLevel?: string | null;
  certifiedLevelIcon?: string;
  certifiedLevelName?: string;
  certificationDate?: string;
  currentXP: number;
  totalXP: number;
  xpForNextLevel: number;
  progressPercentage: number;
  nextLevel?: string;
  jungleCoins: number;
  loyaltyTier: string;
  loyaltyTierIcon: string;
  loyaltyDiscount: number;
  totalSpent: number;
  consecutiveDays: number;
  rank?: number;
}

export interface UserBadge extends Badge {}

export interface LeaderboardEntry {
  userId: number;
  userName: string;
  userPhoto: string;
  totalPoints: number;
  level: string;
  badgeCount: number;
  rank: number;
}

@Injectable({
  providedIn: 'root'
})
export class GamificationService {
  private apiUrl = `${environment.apiUrl}/gamification`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Get user level and points
   */
  getUserLevel(userId: number): Observable<UserLevel> {
    return this.http.get<UserLevel>(
      `${this.apiUrl}/users/${userId}/level`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Get user badges
   */
  getUserBadges(userId: number): Observable<UserBadge[]> {
    return this.http.get<UserBadge[]>(
      `${this.apiUrl}/users/${userId}/badges`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Get new badges (not yet seen)
   */
  getNewBadges(userId: number): Observable<UserBadge[]> {
    return this.http.get<UserBadge[]>(
      `${this.apiUrl}/users/${userId}/badges/new`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Mark badges as seen
   */
  markBadgesAsSeen(userId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/users/${userId}/badges/mark-seen`,
      {},
      { headers: this.getHeaders() }
    );
  }

  /**
   * Add XP to user
   */
  addXP(userId: number, xp: number, reason: string): Observable<UserLevel> {
    return this.http.post<UserLevel>(
      `${this.apiUrl}/users/${userId}/xp`,
      { xp, reason },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Add coins to user
   */
  addCoins(userId: number, coins: number, reason: string): Observable<UserLevel> {
    return this.http.post<UserLevel>(
      `${this.apiUrl}/users/${userId}/coins`,
      { coins, reason },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Spend coins
   */
  spendCoins(userId: number, coins: number): Observable<UserLevel> {
    return this.http.post<UserLevel>(
      `${this.apiUrl}/users/${userId}/coins/spend`,
      { coins },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Award badge to user
   */
  awardBadge(userId: number, badgeCode: string): Observable<UserBadge> {
    return this.http.post<UserBadge>(
      `${this.apiUrl}/users/${userId}/badges`,
      { badgeCode },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Submit assessment test result
   */
  submitAssessment(userId: number, assessedLevel: string): Observable<UserLevel> {
    return this.http.post<UserLevel>(
      `${this.apiUrl}/users/${userId}/assessment`,
      { assessedLevel },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Certify a level after paid exam
   */
  certifyLevel(userId: number, certifiedLevel: string): Observable<UserLevel> {
    return this.http.post<UserLevel>(
      `${this.apiUrl}/users/${userId}/certify`,
      { certifiedLevel },
      { headers: this.getHeaders() }
    );
  }

  // ============ ADMIN METHODS ============

  /**
   * Get all user levels (ADMIN)
   */
  getAllUserLevels(): Observable<UserLevel[]> {
    return this.http.get<UserLevel[]>(
      `${this.apiUrl}/admin/users/levels`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Update assessment level (ADMIN)
   */
  adminUpdateAssessment(userId: number, assessedLevel: string): Observable<UserLevel> {
    return this.http.put<UserLevel>(
      `${this.apiUrl}/admin/users/${userId}/assessment`,
      { assessedLevel },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Certify level (ADMIN)
   */
  adminCertifyLevel(userId: number, certifiedLevel: string): Observable<UserLevel> {
    return this.http.put<UserLevel>(
      `${this.apiUrl}/admin/users/${userId}/certify`,
      { certifiedLevel },
      { headers: this.getHeaders() }
    );
  }

  /**
   * Revoke certification (ADMIN)
   */
  adminRevokeCertification(userId: number): Observable<UserLevel> {
    return this.http.delete<UserLevel>(
      `${this.apiUrl}/admin/users/${userId}/certification`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Get global statistics (ADMIN)
   */
  getGlobalStats(): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/admin/stats`,
      { headers: this.getHeaders() }
    );
  }
}
