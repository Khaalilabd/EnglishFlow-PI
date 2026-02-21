import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { MessagingService } from '../../../../core/services/messaging.service';
import { WebSocketService } from '../../../../core/services/websocket.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Conversation } from '../../../../core/models/conversation.model';
import { Message, SendMessageRequest, MessageType } from '../../../../core/models/message.model';
import { NewConversationModalComponent } from '../new-conversation-modal/new-conversation-modal.component';

@Component({
  selector: 'app-messaging-container',
  standalone: true,
  imports: [CommonModule, FormsModule, NewConversationModalComponent],
  templateUrl: './messaging-container.component.html',
  styleUrls: ['./messaging-container.component.scss']
})
export class MessagingContainerComponent implements OnInit, OnDestroy {
  @ViewChild('messageInput') messageInput!: ElementRef<HTMLInputElement>;
  
  conversations: Conversation[] = [];
  filteredConversations: Conversation[] = [];
  selectedConversation: Conversation | null = null;
  selectedConversationId: number | null = null;
  messages: Message[] = [];
  currentUserId: number = 0;
  newMessage: string = '';
  searchQuery: string = '';
  isTyping: boolean = false;
  showNewConversationModal: boolean = false;
  showEmojiPicker: boolean = false;
  hoveredMessageId: number | null = null;
  
  popularEmojis: string[] = [
    '😀', '😃', '😄', '😁', '😅', '😂', '🤣', '😊', '😇', '🙂',
    '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚', '😋', '😛',
    '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🥳', '😏', '😒',
    '😞', '😔', '😟', '😕', '🙁', '☹️', '😣', '😖', '😫', '😩',
    '🥺', '😢', '😭', '😤', '😠', '😡', '🤬', '🤯', '😳', '🥵',
    '👍', '👎', '👌', '✌️', '🤞', '🤟', '🤘', '🤙', '👈', '👉',
    '👆', '👇', '☝️', '👏', '🙌', '👐', '🤲', '🤝', '🙏', '✍️',
    '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔',
    '❣️', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '☮️',
    '✨', '⭐', '🌟', '💫', '🔥', '💥', '💯', '✅', '🎉', '🎊'
  ];
  
  private destroy$ = new Subject<void>();
  private typingTimeout: any;

  constructor(
    private messagingService: MessagingService,
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.currentUserId = currentUser.id;
    }
    this.loadConversations();
    this.webSocketService.connect();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.webSocketService.disconnect();
  }

  loadConversations(): void {
    this.messagingService.getConversations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversations) => {
          this.conversations = conversations;
          this.filteredConversations = conversations;
        },
        error: (error) => console.error('Error loading conversations:', error)
      });
  }

  filterConversations(): void {
    if (!this.searchQuery.trim()) {
      this.filteredConversations = this.conversations;
    } else {
      const query = this.searchQuery.toLowerCase();
      this.filteredConversations = this.conversations.filter(conv =>
        this.getTitle(conv).toLowerCase().includes(query)
      );
    }
  }

  selectConversation(id: number): void {
    this.selectedConversationId = id;
    this.messages = [];
    if (this.selectedConversation) {
      this.webSocketService.unsubscribeFromConversation(this.selectedConversation.id);
    }
    this.messagingService.getConversation(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversation) => {
          this.selectedConversation = conversation;
          this.loadMessages(id);
          this.subscribeToWebSocket(id);
          this.markAsRead(id);
        }
      });
  }

  loadMessages(conversationId: number): void {
    this.messagingService.getMessages(conversationId, 0, 50)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.messages = page.content.reverse();
          setTimeout(() => this.scrollToBottom(), 100);
        }
      });
  }

  subscribeToWebSocket(conversationId: number): void {
    this.webSocketService.subscribeToConversation(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          if (message.conversationId === conversationId) {
            const exists = this.messages.some(m => m.id === message.id);
            if (!exists) {
              this.messages = [...this.messages, message];
              setTimeout(() => this.scrollToBottom(), 100);
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
          }
        }
      });
  }

  sendMessage(): void {
    if (!this.selectedConversation || !this.newMessage.trim()) return;
    const request: SendMessageRequest = {
      content: this.newMessage.trim(),
      messageType: MessageType.TEXT
    };
    this.webSocketService.sendMessage(this.selectedConversation.id, request);
    this.newMessage = '';
  }

  onTyping(): void {
    if (!this.selectedConversation) return;
    if (this.typingTimeout) clearTimeout(this.typingTimeout);
    this.webSocketService.sendTypingIndicator(this.selectedConversation.id, true);
    this.typingTimeout = setTimeout(() => {
      this.webSocketService.sendTypingIndicator(this.selectedConversation!.id, false);
    }, 2000);
  }

  markAsRead(conversationId: number): void {
    this.messagingService.markAsRead(conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const conv = this.conversations.find(c => c.id === conversationId);
          if (conv) conv.unreadCount = 0;
        }
      });
  }

  scrollToBottom(): void {
    const messagesArea = document.querySelector('.messages-area');
    if (messagesArea) {
      messagesArea.scrollTop = messagesArea.scrollHeight;
    }
  }

  getTitle(conv: Conversation): string {
    if (conv.title) return conv.title;
    const other = conv.participants.find(p => p.userId !== this.currentUserId);
    return other?.userName || 'Conversation';
  }

  getAvatar(conv: Conversation): string {
    const other = conv.participants.find(p => p.userId !== this.currentUserId);
    if (other?.userAvatar && !other.userAvatar.includes('ui-avatars.com')) {
      return `http://localhost:8080${other.userAvatar}`;
    }
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(this.getTitle(conv))}&background=667eea&color=fff&bold=true&size=128`;
  }

  isOnline(conv: Conversation): boolean {
    const other = conv.participants.find(p => p.userId !== this.currentUserId);
    return other?.isOnline || false;
  }

  formatTime(date?: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const now = new Date();
    const diff = now.getTime() - d.getTime();
    const mins = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    if (mins < 1) return 'maintenant';
    if (mins < 60) return `${mins}m`;
    if (hours < 24) return `${hours}h`;
    if (days < 7) return `${days}j`;
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
  }

  formatMessageTime(date?: Date): string {
    if (!date) return '';
    const d = new Date(date);
    return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
  }

  onConversationCreated(conversationId: number): void {
    this.showNewConversationModal = false;
    this.loadConversations();
    setTimeout(() => {
      this.selectConversation(conversationId);
    }, 500);
  }

  toggleEmojiPicker(): void {
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  insertEmoji(emoji: string): void {
    this.newMessage += emoji;
    this.showEmojiPicker = false;
    this.messageInput.nativeElement.focus();
  }

  addEmoji(event: any): void {
    const emoji = event.emoji.native;
    this.newMessage += emoji;
    this.messageInput.nativeElement.focus();
  }

  encodeURIComponent(str: string): string {
    return encodeURIComponent(str);
  }

  addReaction(messageId: number, emoji: string): void {
    this.messagingService.toggleReaction(messageId, emoji)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // La mise à jour sera reçue via WebSocket
          console.log('Reaction toggled:', emoji, 'on message', messageId);
        },
        error: (error) => console.error('Error toggling reaction:', error)
      });
  }

  getReactionTooltip(reaction: any): string {
    if (reaction.userNames.length === 0) return '';
    if (reaction.userNames.length === 1) return reaction.userNames[0];
    if (reaction.userNames.length === 2) {
      return `${reaction.userNames[0]} et ${reaction.userNames[1]}`;
    }
    return `${reaction.userNames[0]}, ${reaction.userNames[1]} et ${reaction.userNames.length - 2} autre(s)`;
  }
}
