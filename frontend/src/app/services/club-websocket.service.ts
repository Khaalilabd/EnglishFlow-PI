import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketService } from './websocket.service';
import { environment } from '../../environments/environment';

export interface ClubNotification {
  type: 'CLUB_CREATED' | 'CLUB_UPDATED' | 'MEMBER_JOINED' | 'MEMBER_LEFT' | 'MEMBERSHIP_REQUEST' | 'TASK_CREATED' | 'SUBSCRIPTION_CONFIRMED';
  clubId: number;
  clubName: string;
  message: string;
  data?: any;
  timestamp: string;
}

export interface MemberActivity {
  activityType: 'JOINED' | 'LEFT' | 'PROMOTED' | 'DEMOTED';
  clubId: number;
  userId: number;
  userName: string;
  role: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClubWebSocketService {
  private clubNotifications$ = new BehaviorSubject<ClubNotification | null>(null);
  private memberActivities$ = new BehaviorSubject<MemberActivity | null>(null);
  private globalNotifications$ = new BehaviorSubject<ClubNotification | null>(null);
  
  private subscriptionIds: string[] = [];
  private currentClubId: number | null = null;

  constructor(private wsService: WebSocketService) {}

  /**
   * Connect to Club WebSocket
   */
  async connect(): Promise<void> {
    // Remove /api prefix for WebSocket connections as API Gateway routes WebSockets differently
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/club-service/ws/club`;
    
    try {
      await this.wsService.connect({
        endpoint,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: !environment.production
      });
      console.log('✅ Club WebSocket connected to:', endpoint);
    } catch (error) {
      console.error('❌ Failed to connect to Club WebSocket:', error);
      throw error;
    }
  }

  /**
   * Subscribe to a specific club's notifications
   */
  subscribeToClub(clubId: number): Observable<ClubNotification> {
    if (this.currentClubId === clubId) {
      console.log(`Already subscribed to club ${clubId}`);
      return this.clubNotifications$.asObservable() as Observable<ClubNotification>;
    }

    // Unsubscribe from previous club if any
    if (this.currentClubId !== null) {
      this.unsubscribeFromClub();
    }

    this.currentClubId = clubId;
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/club-service/ws/club`;

    // Subscribe to club notifications
    const clubSubId = this.wsService.subscribe<ClubNotification>(
      `/topic/club/${clubId}`,
      (notification) => {
        console.log('Club notification received:', notification);
        this.clubNotifications$.next(notification);
      },
      endpoint
    );
    this.subscriptionIds.push(clubSubId);

    // Subscribe to member activities
    const memberSubId = this.wsService.subscribe<MemberActivity>(
      `/topic/club/${clubId}/members`,
      (activity) => {
        console.log('Member activity received:', activity);
        this.memberActivities$.next(activity);
      },
      endpoint
    );
    this.subscriptionIds.push(memberSubId);

    console.log(`Subscribed to club ${clubId}`);
    return this.clubNotifications$.asObservable() as Observable<ClubNotification>;
  }

  /**
   * Subscribe to global club notifications
   */
  subscribeToGlobalClubs(): void {
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/club-service/ws/club`;
    
    const globalSubId = this.wsService.subscribe<ClubNotification>(
      '/topic/clubs',
      (notification) => {
        console.log('📢 Global club notification received:', notification);
        this.globalNotifications$.next(notification);
      },
      endpoint
    );
    this.subscriptionIds.push(globalSubId);
    console.log('📡 Subscribed to global clubs');
  }

  /**
   * Unsubscribe from current club
   */
  unsubscribeFromClub(): void {
    this.subscriptionIds.forEach(id => this.wsService.unsubscribe(id));
    this.subscriptionIds = [];
    this.currentClubId = null;
    console.log('Unsubscribed from club');
  }

  /**
   * Send a message to a club
   */
  sendClubMessage(clubId: number, message: Partial<ClubNotification>): void {
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/club-service/ws/club`;
    this.wsService.send(`/app/club/${clubId}/message`, message, endpoint);
  }

  /**
   * Disconnect from Club WebSocket
   */
  async disconnect(): Promise<void> {
    this.unsubscribeFromClub();
    await this.wsService.disconnect();
    console.log('Club WebSocket disconnected');
  }

  /**
   * Get club notifications as Observable
   */
  getClubNotifications(): Observable<ClubNotification | null> {
    return this.clubNotifications$.asObservable();
  }

  /**
   * Get member activities as Observable
   */
  getMemberActivities(): Observable<MemberActivity | null> {
    return this.memberActivities$.asObservable();
  }

  /**
   * Get global notifications as Observable
   */
  getGlobalNotifications(): Observable<ClubNotification | null> {
    return this.globalNotifications$.asObservable();
  }

  /**
   * Check if connected
   */
  isConnected(): boolean {
    return this.wsService.isConnected();
  }

  /**
   * Get connection status
   */
  getConnectionStatus(): Observable<boolean> {
    return this.wsService.getConnectionStatus();
  }
}
