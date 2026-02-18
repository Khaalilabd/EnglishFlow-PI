import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Conversation } from '../../../../core/models/conversation.model';

@Component({
  selector: 'app-conversation-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="h-full flex flex-col bg-[#111b21]">
      <!-- Header -->
      <div class="p-4 bg-[#202c33]">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-white">Conversations</h2>
          <button 
            (click)="newConversation.emit()"
            class="p-2 text-[#00a884] hover:bg-[#2a3942] rounded-full transition-colors"
            title="Nouvelle conversation"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
          </button>
        </div>
        <div class="relative">
          <input 
            type="text" 
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Rechercher ou démarrer une discussion" 
            class="w-full px-4 py-2 pl-10 bg-[#2a3942] border border-[#3b4a54] rounded-lg text-white placeholder-[#aebac1] focus:outline-none focus:border-[#00a884]"
          />
          <svg class="w-5 h-5 text-[#aebac1] absolute left-3 top-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
      </div>
      
      <!-- Conversations List -->
      <div class="flex-1 overflow-y-auto custom-scrollbar">
        <div *ngIf="filteredConversations.length === 0" class="p-8 text-center">
          <svg class="w-16 h-16 mx-auto mb-3 text-[#667781]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
          </svg>
          <p class="text-[#aebac1]">Aucune conversation</p>
        </div>
        
        <div 
          *ngFor="let conversation of filteredConversations"
          (click)="conversationSelected.emit(conversation.id)"
          class="px-4 py-3 border-b border-[#2a3942] hover:bg-[#202c33] cursor-pointer transition-colors"
          [ngClass]="{'bg-[#2a3942]': selectedConversationId === conversation.id}"
        >
          <div class="flex items-center gap-3">
            <!-- Avatar -->
            <div class="relative flex-shrink-0">
              <img 
                [src]="getConversationAvatar(conversation)" 
                [alt]="getConversationTitle(conversation)" 
                class="w-12 h-12 rounded-full object-cover ring-2 ring-[#2a3942]"
              >
              <span 
                *ngIf="isParticipantOnline(conversation)"
                class="absolute bottom-0 right-0 w-3 h-3 bg-[#00a884] border-2 border-[#111b21] rounded-full"
              ></span>
            </div>
            
            <!-- Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-1">
                <h3 class="font-medium text-white truncate">
                  {{ getConversationTitle(conversation) }}
                </h3>
                <span class="text-xs text-[#aebac1] flex-shrink-0 ml-2">
                  {{ formatTime(conversation.lastMessageAt) }}
                </span>
              </div>
              <div class="flex items-center justify-between">
                <p class="text-sm text-[#aebac1] truncate flex-1">
                  {{ conversation.lastMessage?.content || 'Aucun message' }}
                </p>
                <!-- Unread Badge -->
                <span 
                  *ngIf="conversation.unreadCount > 0"
                  class="ml-2 px-2 py-0.5 bg-[#00a884] text-[#111b21] text-xs rounded-full font-semibold flex-shrink-0"
                >
                  {{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar {
      width: 6px;
    }
    
    .custom-scrollbar::-webkit-scrollbar-track {
      background: #111b21;
    }
    
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: #374045;
      border-radius: 3px;
    }
    
    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
      background: #4a5459;
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
      console.log('📋 ConversationList: Conversations changed', this.conversations);
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
    
    // For direct conversations, show the other participant's name
    const otherParticipant = conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.userName || 'Conversation';
  }

  getConversationAvatar(conversation: Conversation): string {
    const otherParticipant = conversation.participants.find(
      p => p.userId !== this.currentUserId
    );
    
    return otherParticipant?.userAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(this.getConversationTitle(conversation))}&background=2D5757&color=fff`;
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
    
    if (diffMins < 1) return 'À l\'instant';
    if (diffMins < 60) return `${diffMins}m`;
    if (diffHours < 24) return `${diffHours}h`;
    if (diffDays < 7) return `${diffDays}j`;
    
    return messageDate.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
  }
}
