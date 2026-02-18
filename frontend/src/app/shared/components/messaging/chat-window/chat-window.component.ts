import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, AfterViewChecked, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Conversation } from '../../../../core/models/conversation.model';
import { Message } from '../../../../core/models/message.model';
import { MessageBubbleComponent } from '../message-bubble/message-bubble.component';
import { TypingIndicatorComponent } from '../typing-indicator/typing-indicator.component';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule, MessageBubbleComponent, TypingIndicatorComponent],
  template: `
    <div class="chat-window">
      <!-- Chat Header -->
      <div class="chat-header">
        <div class="header-user-info">
          <div class="user-avatar">
            <img [src]="getConversationAvatar()" [alt]="getConversationTitle()">
            <span *ngIf="isOnline()" class="status-dot"></span>
          </div>
          <div class="user-details">
            <h3 class="user-name">{{ getConversationTitle() }}</h3>
            <span class="user-status">{{ isOnline() ? 'En ligne' : 'Hors ligne' }}</span>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn" title="Appel vocal">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
            </svg>
          </button>
          <button class="action-btn" title="Appel vidéo">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
            </svg>
          </button>
          <button class="action-btn" title="Plus d'options">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Messages Area -->
      <div #messagesContainer class="messages-area" (scroll)="onScroll($event)">
        <!-- Load More -->
        <div *ngIf="messages.length >= 50" class="load-more-container">
          <button (click)="loadMore.emit()" class="btn-load-more">
            Charger plus de messages
          </button>
        </div>

        <!-- Messages -->
        <div *ngFor="let message of filteredMessages; trackBy: trackByMessageId" class="message-wrapper">
          <div class="message" [class.message-mine]="message.senderId === currentUserId">
            <div *ngIf="message.senderId !== currentUserId" class="message-avatar">
              <img [src]="getConversationAvatar()" [alt]="getConversationTitle()">
            </div>
            <div class="message-content">
              <div class="message-bubble" [class.bubble-mine]="message.senderId === currentUserId">
                <p class="message-text">{{ message.content }}</p>
                <span class="message-time">{{ formatMessageTime(message.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Typing Indicator -->
        <div *ngIf="isTyping" class="message-wrapper">
          <div class="message">
            <div class="message-avatar">
              <img [src]="getConversationAvatar()" [alt]="typingUserName">
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Message Input -->
      <div class="message-input-container">
        <div class="input-wrapper">
          <button class="input-action-btn" title="Emoji">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </button>
          
          <input 
            [(ngModel)]="newMessage"
            (keyup.enter)="sendMessage()"
            (input)="onTyping()"
            type="text" 
            placeholder="Type a message..." 
            class="message-input"
          />
          
          <button class="input-action-btn" (click)="fileInput.click()" title="Joindre un fichier">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"></path>
            </svg>
          </button>
          <input #fileInput type="file" (change)="onFileSelected($event)" class="hidden" />
          
          <button 
            *ngIf="newMessage.trim()"
            (click)="sendMessage()"
            class="btn-send"
          >
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .chat-window {
      height: 100%;
      display: flex;
      flex-direction: column;
      background: #fff;
    }

    /* Header */
    .chat-header {
      padding: 1.25rem 1.5rem;
      border-bottom: 1px solid #f0f0f0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: #fff;
    }

    .header-user-info {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .user-avatar {
      position: relative;
    }

    .user-avatar img {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      object-fit: cover;
    }

    .status-dot {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 12px;
      height: 12px;
      background: #10b981;
      border: 2px solid #fff;
      border-radius: 50%;
    }

    .user-details {
      display: flex;
      flex-direction: column;
    }

    .user-name {
      font-size: 16px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
    }

    .user-status {
      font-size: 13px;
      color: #10b981;
    }

    .header-actions {
      display: flex;
      gap: 8px;
    }

    .action-btn {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      border: none;
      background: #f9fafb;
      color: #6b7280;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .action-btn:hover {
      background: #F7EDE2;
      color: #2D5757;
    }

    .action-btn svg {
      width: 20px;
      height: 20px;
    }

    /* Messages Area */
    .messages-area {
      flex: 1;
      overflow-y: auto;
      padding: 1.5rem;
      background: #fafafa;
      /* Cacher la scrollbar mais garder le scroll */
      scrollbar-width: none; /* Firefox */
      -ms-overflow-style: none; /* IE et Edge */
    }

    .messages-area::-webkit-scrollbar {
      display: none; /* Chrome, Safari, Opera */
    }

    .load-more-container {
      text-align: center;
      margin-bottom: 1rem;
    }

    .btn-load-more {
      padding: 8px 20px;
      background: #fff;
      border: 1px solid #e5e7eb;
      border-radius: 20px;
      font-size: 13px;
      color: #6b7280;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .btn-load-more:hover {
      background: #f9fafb;
      border-color: #2D5757;
      color: #2D5757;
    }

    /* Messages */
    .message-wrapper {
      margin-bottom: 1rem;
    }

    .message {
      display: flex;
      gap: 10px;
      align-items: flex-end;
    }

    .message.message-mine {
      flex-direction: row-reverse;
    }

    .message-avatar {
      flex-shrink: 0;
    }

    .message-avatar img {
      width: 32px;
      height: 32px;
      border-radius: 10px;
      object-fit: cover;
    }

    .message-content {
      max-width: 60%;
    }

    .message-bubble {
      padding: 12px 16px;
      border-radius: 16px;
      background: #fff;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      position: relative;
    }

    .message-bubble.bubble-mine {
      background: linear-gradient(135deg, #2D5757 0%, #3D3D60 100%);
      color: #fff;
    }

    .message-text {
      font-size: 14px;
      line-height: 1.5;
      margin: 0 0 4px 0;
      color: #1a1a1a;
      word-wrap: break-word;
    }

    .bubble-mine .message-text {
      color: #fff;
    }

    .message-time {
      font-size: 11px;
      color: #9ca3af;
      display: block;
    }

    .bubble-mine .message-time {
      color: rgba(255, 255, 255, 0.7);
    }

    /* Typing Indicator */
    .typing-indicator {
      padding: 12px 16px;
      background: #fff;
      border-radius: 16px;
      display: inline-flex;
      gap: 4px;
      align-items: center;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }

    .typing-indicator span {
      width: 8px;
      height: 8px;
      background: #9ca3af;
      border-radius: 50%;
      animation: typing 1.4s infinite;
    }

    .typing-indicator span:nth-child(2) {
      animation-delay: 0.2s;
    }

    .typing-indicator span:nth-child(3) {
      animation-delay: 0.4s;
    }

    @keyframes typing {
      0%, 60%, 100% {
        transform: translateY(0);
        opacity: 0.7;
      }
      30% {
        transform: translateY(-10px);
        opacity: 1;
      }
    }

    /* Message Input */
    .message-input-container {
      padding: 1rem 1.5rem;
      border-top: 1px solid #f0f0f0;
      background: #fff;
    }

    .input-wrapper {
      display: flex;
      align-items: center;
      gap: 10px;
      background: #f9fafb;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      padding: 8px 12px;
      transition: all 0.2s ease;
    }

    .input-wrapper:focus-within {
      border-color: #2D5757;
      background: #fff;
      box-shadow: 0 0 0 3px rgba(45, 87, 87, 0.1);
    }

    .input-action-btn {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      border: none;
      background: transparent;
      color: #6b7280;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;
      flex-shrink: 0;
    }

    .input-action-btn:hover {
      background: #e5e7eb;
      color: #2D5757;
    }

    .input-action-btn svg {
      width: 20px;
      height: 20px;
    }

    .message-input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: 14px;
      color: #1a1a1a;
      padding: 8px 4px;
    }

    .message-input:focus {
      outline: none;
    }

    .message-input::placeholder {
      color: #9ca3af;
    }

    .btn-send {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      border: none;
      background: linear-gradient(135deg, #2D5757 0%, #3D3D60 100%);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;
      flex-shrink: 0;
    }

    .btn-send:hover {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(45, 87, 87, 0.3);
    }

    .btn-send svg {
      width: 18px;
      height: 18px;
    }

    .hidden {
      display: none;
    }
  `]
})
export class ChatWindowComponent implements AfterViewChecked, OnChanges {
  @Input() conversation!: Conversation;
  @Input() messages: Message[] = [];
  @Input() currentUserId: number = 0;
  @Input() isTyping: boolean = false;
  @Input() typingUserName: string = '';
  @Output() messageSent = new EventEmitter<string>();
  @Output() loadMore = new EventEmitter<void>();
  @Output() typing = new EventEmitter<boolean>();

  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  newMessage: string = '';
  searchQuery: string = '';
  selectedFile: File | null = null;
  filteredMessages: Message[] = [];
  
  private shouldScrollToBottom = true;
  private typingTimeout: any;

  ngOnChanges(): void {
    this.shouldScrollToBottom = true;
    this.filterMessages();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  private filterMessages(): void {
    if (!this.searchQuery.trim()) {
      this.filteredMessages = this.messages;
    } else {
      const query = this.searchQuery.toLowerCase();
      this.filteredMessages = this.messages.filter(msg => 
        msg.content.toLowerCase().includes(query)
      );
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      console.log('File selected:', file.name);
    }
  }

  getConversationTitle(): string {
    if (this.conversation.title) {
      return this.conversation.title;
    }
    
    const otherParticipant = this.conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.userName || 'Conversation';
  }

  getConversationAvatar(): string {
    const otherParticipant = this.conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    if (otherParticipant?.userAvatar && !otherParticipant.userAvatar.includes('ui-avatars.com')) {
      return `http://localhost:8080${otherParticipant.userAvatar}`;
    }
    
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(this.getConversationTitle())}&background=2D5757&color=fff&bold=true&size=128`;
  }

  isOnline(): boolean {
    const otherParticipant = this.conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.isOnline || false;
  }

  sendMessage(): void {
    if (this.newMessage.trim()) {
      this.messageSent.emit(this.newMessage);
      this.newMessage = '';
      this.shouldScrollToBottom = true;
      this.typing.emit(false);
    }
  }

  onTyping(): void {
    if (this.typingTimeout) {
      clearTimeout(this.typingTimeout);
    }

    this.typing.emit(true);

    this.typingTimeout = setTimeout(() => {
      this.typing.emit(false);
    }, 2000);
  }

  onScroll(event: any): void {
    const element = event.target;
    if (element.scrollTop === 0) {
      this.loadMore.emit();
    }
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        const element = this.messagesContainer.nativeElement;
        element.scrollTop = element.scrollHeight;
      }
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }

  formatMessageTime(date?: Date): string {
    if (!date) return '';
    
    const messageDate = new Date(date);
    const hours = messageDate.getHours().toString().padStart(2, '0');
    const minutes = messageDate.getMinutes().toString().padStart(2, '0');
    
    return `${hours}:${minutes}`;
  }

  trackByMessageId(index: number, message: Message): number {
    return message.id;
  }
}
