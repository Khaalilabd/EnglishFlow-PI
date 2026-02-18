import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { MessagingService } from '../../../../core/services/messaging.service';
import { WebSocketService } from '../../../../core/services/websocket.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Conversation } from '../../../../core/models/conversation.model';
import { Message, SendMessageRequest, MessageType } from '../../../../core/models/message.model';
import { ConversationListComponent } from '../conversation-list/conversation-list.component';
import { ChatWindowComponent } from '../chat-window/chat-window.component';
import { NewConversationModalComponent } from '../new-conversation-modal/new-conversation-modal.component';

@Component({
  selector: 'app-messaging-container',
  standalone: true,
  imports: [CommonModule, ConversationListComponent, ChatWindowComponent, NewConversationModalComponent],
  template: `
    <!-- Container Principal Pleine Largeur -->
    <div class="messaging-page">
      <!-- Header avec Titre -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <svg class="title-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
            </svg>
            <div>
              <h1 class="page-title">Messages</h1>
              <p class="page-subtitle">Communiquez avec vos tuteurs et étudiants</p>
            </div>
          </div>
          <div class="breadcrumb">
            <span class="breadcrumb-item">Home</span>
            <span class="breadcrumb-separator">/</span>
            <span class="breadcrumb-item active">Chats</span>
          </div>
        </div>
      </div>

      <!-- Container de Messagerie Pleine Largeur -->
      <div class="messaging-container">
        <!-- Sidebar - Liste des conversations -->
        <div class="conversations-panel">
          <app-conversation-list
            [conversations]="conversations"
            [selectedConversationId]="selectedConversationId"
            [currentUserId]="currentUserId"
            (conversationSelected)="onConversationSelected($event)"
            (newConversation)="onNewConversation()"
          ></app-conversation-list>
        </div>
        
        <!-- Zone de Chat Principale -->
        <div class="chat-panel">
          <app-chat-window
            *ngIf="selectedConversation"
            [conversation]="selectedConversation"
            [messages]="messages"
            [currentUserId]="currentUserId"
            [isTyping]="isTyping"
            [typingUserName]="typingUserName"
            (messageSent)="onMessageSent($event)"
            (loadMore)="onLoadMore()"
            (typing)="onTyping($event)"
          ></app-chat-window>
          
          <!-- État Vide -->
          <div *ngIf="!selectedConversation" class="empty-chat-state">
            <div class="empty-content">
              <div class="empty-icon-container">
                <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                </svg>
              </div>
              <h3 class="empty-title">Sélectionnez une conversation</h3>
              <p class="empty-description">
                Choisissez une conversation dans la liste pour commencer à discuter
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal nouvelle conversation -->
    <app-new-conversation-modal
      *ngIf="showNewConversationModal"
      (close)="showNewConversationModal = false"
      (conversationCreated)="onConversationCreated($event)"
    ></app-new-conversation-modal>
  `,
  styles: [`
    .messaging-page {
      width: 100%;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      padding: 2rem;
    }

    /* Page Header */
    .page-header {
      max-width: 1400px;
      width: 100%;
      margin: 0 auto 2rem;
    }

    .header-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .title-section {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .title-icon {
      width: 40px;
      height: 40px;
      color: #2D5757;
      flex-shrink: 0;
    }

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: #1a1a1a;
      margin: 0;
      line-height: 1.2;
    }

    .page-subtitle {
      font-size: 14px;
      color: #6b7280;
      margin: 0;
      margin-top: 2px;
    }

    .breadcrumb {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 14px;
    }

    .breadcrumb-item {
      color: #6b7280;
      transition: color 0.2s ease;
    }

    .breadcrumb-item:not(.active):hover {
      color: #2D5757;
      cursor: pointer;
    }

    .breadcrumb-item.active {
      color: #1a1a1a;
      font-weight: 500;
    }

    .breadcrumb-separator {
      color: #d1d5db;
    }

    /* Container Principal - CENTRÉ avec max-width */
    .messaging-container {
      max-width: 1400px;
      width: 100%;
      margin: 0 auto;
      height: calc(100vh - 200px);
      min-height: 600px;
      display: grid;
      grid-template-columns: 380px 1fr;
      gap: 0;
      overflow: hidden;
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      border: 1px solid #e5e7eb;
    }

    .conversations-panel {
      background: #fff;
      border-right: 1px solid #e5e7eb;
      border-radius: 16px 0 0 16px;
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }

    .chat-panel {
      background: #fff;
      border-radius: 0 16px 16px 0;
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }

    /* État Vide */
    .empty-chat-state {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 3rem;
      background: #fafafa;
    }

    .empty-content {
      text-align: center;
      max-width: 400px;
    }

    .empty-icon-container {
      margin-bottom: 1.5rem;
    }

    .empty-icon {
      width: 80px;
      height: 80px;
      color: #d1d5db;
      margin: 0 auto;
    }

    .empty-title {
      font-size: 20px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 0.5rem 0;
    }

    .empty-description {
      font-size: 14px;
      color: #6b7280;
      line-height: 1.6;
      margin: 0;
    }

    /* Responsive */
    @media (max-width: 1200px) {
      .messaging-container {
        grid-template-columns: 340px 1fr;
      }
    }

    @media (max-width: 1024px) {
      .messaging-page {
        padding: 1.5rem;
      }

      .page-header {
        margin-bottom: 1.5rem;
      }

      .page-title {
        font-size: 24px;
      }

      .messaging-container {
        grid-template-columns: 300px 1fr;
        height: calc(100vh - 180px);
      }
    }

    @media (max-width: 768px) {
      .messaging-page {
        padding: 1rem;
      }

      .page-header {
        margin-bottom: 1rem;
      }

      .header-content {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.75rem;
      }

      .title-icon {
        width: 32px;
        height: 32px;
      }

      .page-title {
        font-size: 20px;
      }

      .page-subtitle {
        font-size: 13px;
      }

      .messaging-container {
        grid-template-columns: 1fr;
        height: calc(100vh - 160px);
      }

      .conversations-panel {
        display: none;
        border-radius: 16px;
      }

      .conversations-panel.mobile-visible {
        display: flex;
      }

      .chat-panel {
        display: none;
        border-radius: 16px;
      }

      .chat-panel.mobile-visible {
        display: flex;
      }
    }
  `]
})
export class MessagingContainerComponent implements OnInit, OnDestroy {
  conversations: Conversation[] = [];
  selectedConversation: Conversation | null = null;
  selectedConversationId: number | null = null;
  messages: Message[] = [];
  currentUserId: number = 0;
  isTyping: boolean = false;
  typingUserName: string = '';
  showNewConversationModal: boolean = false;
  
  private destroy$ = new Subject<void>();
  private currentPage = 0;
  private pageSize = 50;

  constructor(
    private messagingService: MessagingService,
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log('🚀 MessagingContainer: Initializing...');
    
    const currentUser = this.authService.currentUserValue;
    console.log('👤 Current user:', currentUser);
    
    if (currentUser) {
      this.currentUserId = currentUser.id;
      console.log('✅ User ID set:', this.currentUserId);
    } else {
      console.error('❌ No current user found!');
    }

    console.log('📡 Loading conversations...');
    this.loadConversations();
    
    console.log('🔌 Connecting WebSocket...');
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.webSocketService.disconnect();
  }

  private loadConversations(): void {
    console.log('📞 API Call: Getting conversations...');
    this.messagingService.getConversations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversations) => {
          console.log('✅ Conversations loaded:', conversations);
          this.conversations = conversations;
        },
        error: (error) => {
          console.error('❌ Error loading conversations:', error);
          console.error('Error details:', {
            status: error.status,
            message: error.message,
            url: error.url
          });
        }
      });
  }

  private connectWebSocket(): void {
    this.webSocketService.connect();
  }

  onConversationSelected(conversationId: number): void {
    this.selectedConversationId = conversationId;
    this.currentPage = 0;
    this.messages = [];
    
    if (this.selectedConversation) {
      this.webSocketService.unsubscribeFromConversation(this.selectedConversation.id);
    }
    
    this.messagingService.getConversation(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversation) => {
          this.selectedConversation = conversation;
          this.loadMessages(conversationId);
          this.subscribeToConversation(conversationId);
          this.markAsRead(conversationId);
        },
        error: (error) => {
          console.error('Error loading conversation:', error);
        }
      });
  }

  private loadMessages(conversationId: number): void {
    this.messagingService.getMessages(conversationId, this.currentPage, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.messages = [...page.content.reverse(), ...this.messages];
        },
        error: (error) => {
          console.error('Error loading messages:', error);
        }
      });
  }

  private subscribeToConversation(conversationId: number): void {
    this.webSocketService.subscribeToConversation(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          console.log('📨 New message received via WebSocket:', message);
          if (message.conversationId === conversationId) {
            const exists = this.messages.some(m => m.id === message.id);
            if (!exists) {
              this.messages.push(message);
            }
            
            const conv = this.conversations.find(c => c.id === conversationId);
            if (conv) {
              conv.lastMessage = message;
              conv.lastMessageAt = message.createdAt;
              if (message.senderId !== this.currentUserId) {
                conv.unreadCount = (conv.unreadCount || 0) + 1;
              }
            }
            
            if (this.selectedConversationId === conversationId) {
              this.markAsRead(conversationId);
            }
          }
        }
      });

    this.webSocketService.subscribeToTypingIndicator(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (indicator) => {
          if (indicator.userId !== this.currentUserId) {
            this.isTyping = indicator.isTyping;
            this.typingUserName = indicator.userName;
            
            if (indicator.isTyping) {
              setTimeout(() => {
                this.isTyping = false;
              }, 3000);
            }
          }
        }
      });
  }

  onMessageSent(content: string): void {
    if (!this.selectedConversation || !content.trim()) {
      return;
    }

    const request: SendMessageRequest = {
      content: content.trim(),
      messageType: MessageType.TEXT
    };

    this.messagingService.sendMessage(this.selectedConversation.id, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          console.log('✅ Message sent successfully:', message);
          const exists = this.messages.some(m => m.id === message.id);
          if (!exists) {
            this.messages.push(message);
          }
          
          const conv = this.conversations.find(c => c.id === this.selectedConversation?.id);
          if (conv) {
            conv.lastMessage = message;
            conv.lastMessageAt = message.createdAt;
          }
        },
        error: (error) => {
          console.error('❌ Error sending message:', error);
        }
      });
  }

  onLoadMore(): void {
    if (this.selectedConversation) {
      this.currentPage++;
      this.loadMessages(this.selectedConversation.id);
    }
  }

  onTyping(isTyping: boolean): void {
    if (this.selectedConversation) {
      this.webSocketService.sendTypingIndicator(this.selectedConversation.id, isTyping);
    }
  }

  private markAsRead(conversationId: number): void {
    this.messagingService.markAsRead(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const conv = this.conversations.find(c => c.id === conversationId);
          if (conv) {
            conv.unreadCount = 0;
          }
        },
        error: (error) => {
          console.error('Error marking as read:', error);
        }
      });
  }

  onNewConversation(): void {
    console.log('🆕 Opening new conversation modal');
    this.showNewConversationModal = true;
  }

  onConversationCreated(conversationId: number): void {
    console.log('✅ Conversation created with ID:', conversationId);
    this.loadConversations();
    setTimeout(() => {
      this.onConversationSelected(conversationId);
    }, 500);
  }
}
