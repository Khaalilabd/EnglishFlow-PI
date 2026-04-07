import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketService } from './websocket.service';
import { environment } from '../../environments/environment';

export interface EventNotification {
  type: 'EVENT_CREATED' | 'EVENT_UPDATED' | 'EVENT_CANCELLED' | 'PARTICIPANT_JOINED' | 'PARTICIPANT_LEFT' | 'SUBSCRIPTION_CONFIRMED';
  eventId: number;
  eventTitle: string;
  message: string;
  data?: any;
  timestamp: string;
}

export interface ParticipantActivity {
  activityType: 'JOINED' | 'LEFT' | 'CHECKED_IN';
  eventId: number;
  userId: number;
  userName: string;
  currentParticipants: number;
  maxParticipants: number;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventWebSocketService {
  private eventNotifications$ = new BehaviorSubject<EventNotification | null>(null);
  private participantActivities$ = new BehaviorSubject<ParticipantActivity | null>(null);
  private globalNotifications$ = new BehaviorSubject<EventNotification | null>(null);
  
  private subscriptionIds: string[] = [];
  private currentEventId: number | null = null;

  constructor(private wsService: WebSocketService) {}

  /**
   * Connect to Event WebSocket
   */
  async connect(): Promise<void> {
    // Remove /api prefix for WebSocket connections as API Gateway routes WebSockets differently
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/event-service/ws/event`;
    
    try {
      await this.wsService.connect({
        endpoint,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: !environment.production
      });
      console.log('✅ Event WebSocket connected to:', endpoint);
    } catch (error) {
      console.error('❌ Failed to connect to Event WebSocket:', error);
      throw error;
    }
  }

  /**
   * Subscribe to a specific event's notifications
   */
  subscribeToEvent(eventId: number): void {
    if (this.currentEventId === eventId) {
      console.log(`Already subscribed to event ${eventId}`);
      return;
    }

    // Unsubscribe from previous event if any
    if (this.currentEventId !== null) {
      this.unsubscribeFromEvent();
    }

    this.currentEventId = eventId;
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/event-service/ws/event`;

    // Subscribe to event notifications
    const eventSubId = this.wsService.subscribe<EventNotification>(
      `/topic/event/${eventId}`,
      (notification) => {
        console.log('Event notification received:', notification);
        this.eventNotifications$.next(notification);
      },
      endpoint
    );
    this.subscriptionIds.push(eventSubId);

    // Subscribe to participant activities
    const participantSubId = this.wsService.subscribe<ParticipantActivity>(
      `/topic/event/${eventId}/participants`,
      (activity) => {
        console.log('Participant activity received:', activity);
        this.participantActivities$.next(activity);
      },
      endpoint
    );
    this.subscriptionIds.push(participantSubId);

    console.log(`Subscribed to event ${eventId}`);
  }

  /**
   * Subscribe to global event notifications
   */
  subscribeToGlobalEvents(): void {
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/event-service/ws/event`;
    
    const globalSubId = this.wsService.subscribe<EventNotification>(
      '/topic/events',
      (notification) => {
        console.log('📢 Global event notification received:', notification);
        this.globalNotifications$.next(notification);
      },
      endpoint
    );
    this.subscriptionIds.push(globalSubId);
    console.log('📡 Subscribed to global events');
  }

  /**
   * Unsubscribe from current event
   */
  unsubscribeFromEvent(): void {
    this.subscriptionIds.forEach(id => this.wsService.unsubscribe(id));
    this.subscriptionIds = [];
    this.currentEventId = null;
    console.log('Unsubscribed from event');
  }

  /**
   * Send a message to an event
   */
  sendEventMessage(eventId: number, message: Partial<EventNotification>): void {
    const baseUrl = environment.apiUrl.replace('/api', '');
    const endpoint = `${baseUrl}/event-service/ws/event`;
    this.wsService.send(`/app/event/${eventId}/message`, message, endpoint);
  }

  /**
   * Disconnect from Event WebSocket
   */
  async disconnect(): Promise<void> {
    this.unsubscribeFromEvent();
    await this.wsService.disconnect();
    console.log('Event WebSocket disconnected');
  }

  /**
   * Get event notifications as Observable
   */
  getEventNotifications(): Observable<EventNotification | null> {
    return this.eventNotifications$.asObservable();
  }

  /**
   * Get participant activities as Observable
   */
  getParticipantActivities(): Observable<ParticipantActivity | null> {
    return this.participantActivities$.asObservable();
  }

  /**
   * Get global notifications as Observable
   */
  getGlobalNotifications(): Observable<EventNotification | null> {
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
