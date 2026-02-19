import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Conversation, CreateConversationRequest } from '../models/conversation.model';
import { Message, SendMessageRequest, Page } from '../models/message.model';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private apiUrl = `${environment.apiUrl}/messaging`; // Via API Gateway

  constructor(private http: HttpClient) {}

  // Conversations
  getConversations(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.apiUrl}/conversations`);
  }

  getConversation(id: number): Observable<Conversation> {
    return this.http.get<Conversation>(`${this.apiUrl}/conversations/${id}`);
  }

  createConversation(request: CreateConversationRequest): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.apiUrl}/conversations`, request);
  }

  // Messages
  getMessages(conversationId: number, page: number = 0, size: number = 50): Observable<Page<Message>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<Page<Message>>(
      `${this.apiUrl}/conversations/${conversationId}/messages`,
      { params }
    );
  }

  sendMessage(conversationId: number, request: SendMessageRequest): Observable<Message> {
    return this.http.post<Message>(
      `${this.apiUrl}/conversations/${conversationId}/messages`,
      request
    );
  }

  markAsRead(conversationId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/conversations/${conversationId}/mark-read`,
      {}
    );
  }

  // Unread count
  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/unread-count`);
  }
}
