import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Message, SendMessageRequest, TypingIndicator } from '../models/message.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client | null = null;
  private connected$ = new BehaviorSubject<boolean>(false);
  private messageSubject = new Subject<Message>();
  private typingSubject = new Subject<TypingIndicator>();
  private subscriptions: Map<string, StompSubscription> = new Map();

  constructor(private authService: AuthService) {}

  connect(): void {
    if (this.stompClient && this.stompClient.connected) {
      console.log('WebSocket already connected');
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      console.error('No auth token available');
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (str) => {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket connected');
        this.connected$.next(true);
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
        this.connected$.next(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      }
    });

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
      this.subscriptions.clear();
      this.stompClient.deactivate();
      this.stompClient = null;
      this.connected$.next(false);
    }
  }

  isConnected(): Observable<boolean> {
    return this.connected$.asObservable();
  }

  subscribeToConversation(conversationId: number): Observable<Message> {
    const destination = `/topic/conversation/${conversationId}`;
    
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket not connected');
      return new Observable();
    }

    // Unsubscribe if already subscribed
    if (this.subscriptions.has(destination)) {
      this.subscriptions.get(destination)?.unsubscribe();
    }

    const subscription = this.stompClient.subscribe(destination, (message) => {
      const msg: Message = JSON.parse(message.body);
      this.messageSubject.next(msg);
    });

    this.subscriptions.set(destination, subscription);

    return this.messageSubject.asObservable();
  }

  subscribeToTypingIndicator(conversationId: number): Observable<TypingIndicator> {
    const destination = `/topic/conversation/${conversationId}/typing`;
    
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket not connected');
      return new Observable();
    }

    const subscription = this.stompClient.subscribe(destination, (message) => {
      const indicator: TypingIndicator = JSON.parse(message.body);
      this.typingSubject.next(indicator);
    });

    this.subscriptions.set(destination + '/typing', subscription);

    return this.typingSubject.asObservable();
  }

  sendMessage(conversationId: number, message: SendMessageRequest): void {
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.stompClient.publish({
      destination: `/app/chat/${conversationId}`,
      body: JSON.stringify(message)
    });
  }

  sendTypingIndicator(conversationId: number, isTyping: boolean): void {
    if (!this.stompClient || !this.stompClient.connected) {
      return;
    }

    this.stompClient.publish({
      destination: `/app/typing/${conversationId}`,
      body: JSON.stringify({ isTyping })
    });
  }

  unsubscribeFromConversation(conversationId: number): void {
    const destination = `/topic/conversation/${conversationId}`;
    const subscription = this.subscriptions.get(destination);
    
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
    }

    const typingDestination = destination + '/typing';
    const typingSubscription = this.subscriptions.get(typingDestination);
    
    if (typingSubscription) {
      typingSubscription.unsubscribe();
      this.subscriptions.delete(typingDestination);
    }
  }
}
