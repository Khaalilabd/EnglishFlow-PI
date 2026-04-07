import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketService } from './websocket.service';
import { environment } from '../../environments/environment';
import { Sponsor } from '../core/models/sponsor.model';

export interface SponsorNotification {
  type: 'CREATED' | 'UPDATED' | 'DELETED';
  sponsorId: number;
  sponsorName: string;
  message: string;
  timestamp: string;
  sponsor?: Sponsor;
}

@Injectable({
  providedIn: 'root'
})
export class SponsorWebSocketService {
  private sponsorNotifications$ = new BehaviorSubject<SponsorNotification | null>(null);
  private subscriptionId: string | null = null;

  constructor(private wsService: WebSocketService) {}

  async connect(): Promise<void> {
    const wsBaseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${wsBaseUrl}/sponsors-service/ws`;
    
    try {
      await this.wsService.connect({
        endpoint,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: !environment.production
      });
      console.log('✅ Sponsor WebSocket connected');
    } catch (error) {
      console.error('❌ Failed to connect to Sponsor WebSocket:', error);
      throw error;
    }
  }

  subscribeToSponsors(): void {
    if (this.subscriptionId) {
      console.log('Already subscribed to sponsors');
      return;
    }

    const wsBaseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${wsBaseUrl}/sponsors-service/ws`;

    this.subscriptionId = this.wsService.subscribe<SponsorNotification>(
      '/topic/sponsors',
      (notification) => {
        console.log('📢 Sponsor notification received:', notification);
        this.sponsorNotifications$.next(notification);
      },
      endpoint
    );

    console.log('📡 Subscribed to sponsors updates');
  }

  unsubscribe(): void {
    if (this.subscriptionId) {
      this.wsService.unsubscribe(this.subscriptionId);
      this.subscriptionId = null;
      console.log('🔌 Unsubscribed from sponsors');
    }
  }

  async disconnect(): Promise<void> {
    this.unsubscribe();
    await this.wsService.disconnect();
    console.log('🔌 Sponsor WebSocket disconnected');
  }

  getSponsorNotifications(): Observable<SponsorNotification | null> {
    return this.sponsorNotifications$.asObservable();
  }

  isConnected(): boolean {
    return this.wsService.isConnected();
  }

  getConnectionStatus(): Observable<boolean> {
    return this.wsService.getConnectionStatus();
  }
}
