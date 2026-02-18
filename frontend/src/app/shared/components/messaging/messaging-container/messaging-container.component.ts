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
    <!-- WhatsApp-style Container -->
    <div class="h-[calc(100vh-120px)] bg-[#111b21] rounded-lg overflow-hidden shadow-2xl">
      <div class="grid grid-cols-12 h-full">
        <!-- Sidebar - Liste des conversations -->
        <div class="col-span-12 md:col-span-4 lg:col-span-3 bg-[#111b21] border-r border-[#2a3942] flex flex-col">
          <app-conversation-list
            [conversations]="conversations"
            [selectedConversationId]="selectedConversationId"
            [currentUserId]="currentUserId"
            (conversationSelected)="onConversationSelected($event)"
            (newConversation)="onNewConversation()"
          ></app-conversation-list>
        </div>
        
        <!-- Main Chat Area -->
        <div class="col-span-12 md:col-span-8 lg:col-span-9 bg-[#0b141a] flex flex-col">
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
          
          <!-- Empty State -->
          <div *ngIf="!selectedConversation" class="flex-1 flex items-center justify-center h-full">
            <div class="text-center px-8">
              <div class="mb-8">
                <svg class="w-32 h-32 mx-auto text-[#54656f]" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2C6.48 2 2 6.48 2 12c0 1.54.36 3 .97 4.29L2 22l5.71-.97C9 21.64 10.46 22 12 22c5.52 0 10-4.48 10-10S17.52 2 12 2zm0 18c-1.38 0-2.68-.29-3.86-.81l-.28-.13-2.89.49.49-2.89-.13-.28C4.79 14.68 4.5 13.38 4.5 12c0-4.14 3.36-7.5 7.5-7.5s7.5 3.36 7.5 7.5-3.36 7.5-7.5 7.5z"/>
                  <path d="M8.5 11h7v2h-7z"/>
                </svg>
              </div>
              <h3 class="text-2xl font-light text-white mb-3">EnglishFlow Messagerie</h3>
              <p class="text-[#aebac1] text-sm max-w-md mx-auto leading-relaxed">
                Envoyez et recevez des messages sans garder votre téléphone connecté.<br/>
                Sélectionnez une conversation pour commencer.
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
    :host {
      display: block;
      height: 100%;
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
    
    // Unsubscribe from previous conversation
    if (this.selectedConversation) {
      this.webSocketService.unsubscribeFromConversation(this.selectedConversation.id);
    }
    
    // Load conversation details
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
          // Reverse to show oldest first
          this.messages = [...page.content.reverse(), ...this.messages];
        },
        error: (error) => {
          console.error('Error loading messages:', error);
        }
      });
  }

  private subscribeToConversation(conversationId: number): void {
    // Subscribe to new messages
    this.webSocketService.subscribeToConversation(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          if (message.conversationId === conversationId) {
            this.messages.push(message);
            
            // Update conversation list
            const conv = this.conversations.find(c => c.id === conversationId);
            if (conv) {
              conv.lastMessage = message;
              conv.lastMessageAt = message.createdAt;
              if (message.senderId !== this.currentUserId) {
                conv.unreadCount++;
              }
            }
            
            // Mark as read if conversation is open
            if (this.selectedConversationId === conversationId) {
              this.markAsRead(conversationId);
            }
          }
        }
      });

    // Subscribe to typing indicator
    this.webSocketService.subscribeToTypingIndicator(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (indicator) => {
          if (indicator.userId !== this.currentUserId) {
            this.isTyping = indicator.isTyping;
            this.typingUserName = indicator.userName;
            
            // Clear typing indicator after 3 seconds
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
          // Message will be received via WebSocket
          console.log('Message sent:', message);
        },
        error: (error) => {
          console.error('Error sending message:', error);
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
          // Update unread count in conversation list
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
    // Recharger les conversations
    this.loadConversations();
    // Sélectionner la nouvelle conversation
    setTimeout(() => {
      this.onConversationSelected(conversationId);
    }, 500);
  }
}
