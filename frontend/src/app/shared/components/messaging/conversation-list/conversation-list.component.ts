import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Conversation } from '../../../../core/models/conversation.model';

@Component({
  selector: 'app-conversation-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="conversation-list-container">
      <!-- Header -->
      <div class="list-header">
        <h2 class="list-title">Chats</h2>
        <button 
          (click)="newConversation.emit()"
          class="btn-new-chat"
          title="Nouvelle conversation"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
          </svg>
        </button>
      </div>

      <!-- Search Bar -->
      <div class="search-container">
        <div class="search-wrapper">
          <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
          <input 
            type="text" 
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Search..." 
            class="search-input"
          />
        </div>
      </div>

      <!-- Conversations List -->
      <div class="conversations-list">
        <div *ngIf="filteredConversations.length === 0" class="empty-list">
          <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
          </svg>
          <p class="empty-text">Aucune conversation</p>
        </div>

        <div 
          *ngFor="let conversation of filteredConversations"
          (click)="conversationSelected.emit(conversation.id)"
          class="conversation-item"
          [class.active]="selectedConversationId === conversation.id"
        >
          <div class="conversation-avatar">
            <img 
              [src]="getConversationAvatar(conversation)" 
              [alt]="getConversationTitle(conversation)"
            >
            <span 
              *ngIf="isParticipantOnline(conversation)"
              class="status-indicator online"
            ></span>
          </div>

          <div class="conversation-content">
            <div class="conversation-header">
              <h3 class="conversation-name">{{ getConversationTitle(conversation) }}</h3>
              <span class="conversation-time">{{ formatTime(conversation.lastMessageAt) }}</span>
            </div>
            <div class="conversation-preview">
              <p class="last-message">
                <span *ngIf="conversation.lastMessage?.senderId === currentUserId" class="you-prefix">Vous: </span>
                {{ conversation.lastMessage?.content || 'Aucun message' }}
              </p>
              <span 
                *ngIf="conversation.unreadCount > 0"
                class="unread-badge"
              >
                {{ conversation.unreadCount > 9 ? '9+' : conversation.unreadCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .conversation-list-container {
      height: 100%;
      display: flex;
      flex-direction: column;
      background: #fff;
    }

    /* Header */
    .list-header {
      padding: 1.5rem 1.5rem 1rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid #f0f0f0;
    }

    .list-title {
      font-size: 24px;
      font-weight: 700;
      color: #1a1a1a;
      margin: 0;
    }

    .btn-new-chat {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, #2D5757 0%, #3D3D60 100%);
      border: none;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .btn-new-chat:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(45, 87, 87, 0.3);
    }

    .btn-new-chat svg {
      width: 20px;
      height: 20px;
    }

    /* Search */
    .search-container {
      padding: 0 1.5rem 1rem;
      border-bottom: 1px solid #f0f0f0;
    }

    .search-wrapper {
      position: relative;
    }

    .search-icon {
      position: absolute;
      left: 14px;
      top: 50%;
      transform: translateY(-50%);
      width: 18px;
      height: 18px;
      color: #9ca3af;
      pointer-events: none;
    }

    .search-input {
      width: 100%;
      padding: 12px 14px 12px 42px;
      border: 1px solid #e5e7eb;
      border-radius: 10px;
      font-size: 14px;
      color: #1a1a1a;
      background: #f9fafb;
      transition: all 0.2s ease;
    }

    .search-input:focus {
      outline: none;
      border-color: #2D5757;
      background: #fff;
      box-shadow: 0 0 0 3px rgba(45, 87, 87, 0.1);
    }

    .search-input::placeholder {
      color: #9ca3af;
    }

    /* Conversations List */
    .conversations-list {
      flex: 1;
      overflow-y: auto;
      /* Cacher la scrollbar mais garder le scroll */
      scrollbar-width: none; /* Firefox */
      -ms-overflow-style: none; /* IE et Edge */
    }

    .conversations-list::-webkit-scrollbar {
      display: none; /* Chrome, Safari, Opera */
    }

    /* Empty State */
    .empty-list {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 3rem 1.5rem;
      text-align: center;
    }

    .empty-icon {
      width: 64px;
      height: 64px;
      color: #d1d5db;
      margin-bottom: 1rem;
    }

    .empty-text {
      color: #9ca3af;
      font-size: 14px;
    }

    /* Conversation Item */
    .conversation-item {
      display: flex;
      gap: 12px;
      padding: 14px 1.5rem;
      cursor: pointer;
      transition: all 0.2s ease;
      border-left: 3px solid transparent;
    }

    .conversation-item:hover {
      background: #f9fafb;
    }

    .conversation-item.active {
      background: #F7EDE2;
      border-left-color: #2D5757;
    }

    /* Avatar */
    .conversation-avatar {
      position: relative;
      flex-shrink: 0;
    }

    .conversation-avatar img {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      object-fit: cover;
    }

    .status-indicator {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      border: 2px solid #fff;
    }

    .status-indicator.online {
      background: #10b981;
    }

    /* Content */
    .conversation-content {
      flex: 1;
      min-width: 0;
    }

    .conversation-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 4px;
    }

    .conversation-name {
      font-size: 15px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .conversation-time {
      font-size: 12px;
      color: #9ca3af;
      flex-shrink: 0;
      margin-left: 8px;
    }

    .conversation-preview {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 8px;
    }

    .last-message {
      font-size: 13px;
      color: #6b7280;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      flex: 1;
    }

    .you-prefix {
      color: #2D5757;
      font-weight: 500;
    }

    .unread-badge {
      background: #2D5757;
      color: #fff;
      font-size: 11px;
      font-weight: 600;
      padding: 2px 7px;
      border-radius: 10px;
      flex-shrink: 0;
      min-width: 20px;
      text-align: center;
    }
  `]
})
export class ConversationListComponent implements OnChanges {
  @Input() conversations: Conversation[] = [];
  @Input() selectedConversationId: number | null = null;
  @Input() currentUserId: number = 0;
  @Output() conversationSelected = new EventEmitter<number>();
  @Output() newConversation = new EventEmitter<void>();

  searchQuery: string = '';
  filteredConversations: Conversation[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['conversations']) {
      this.filterConversations();
    }
  }

  onSearchChange(): void {
    this.filterConversations();
  }

  private filterConversations(): void {
    if (!this.searchQuery.trim()) {
      this.filteredConversations = this.conversations;
    } else {
      const query = this.searchQuery.toLowerCase();
      this.filteredConversations = this.conversations.filter(conv => {
        const title = this.getConversationTitle(conv).toLowerCase();
        const lastMessage = conv.lastMessage?.content.toLowerCase() || '';
        return title.includes(query) || lastMessage.includes(query);
      });
    }
  }

  getConversationTitle(conversation: Conversation): string {
    if (conversation.title) {
      return conversation.title;
    }
    
    const otherParticipant = conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.userName || 'Conversation';
  }

  getConversationAvatar(conversation: Conversation): string {
    const otherParticipant = conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    if (otherParticipant?.userAvatar && !otherParticipant.userAvatar.includes('ui-avatars.com')) {
      return `http://localhost:8080${otherParticipant.userAvatar}`;
    }
    
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(this.getConversationTitle(conversation))}&background=2D5757&color=fff&bold=true&size=128`;
  }

  isParticipantOnline(conversation: Conversation): boolean {
    const otherParticipant = conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.isOnline || false;
  }

  formatTime(date?: Date): string {
    if (!date) return '';
    
    const messageDate = new Date(date);
    const now = new Date();
    const diffMs = now.getTime() - messageDate.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);
    
    if (diffMins < 1) return 'now';
    if (diffMins < 60) return `${diffMins}m`;
    if (diffHours < 24) return `${diffHours}h`;
    if (diffDays < 7) return `${diffDays}d`;
    
    return messageDate.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
  }
}
