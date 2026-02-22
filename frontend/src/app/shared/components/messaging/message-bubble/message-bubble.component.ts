import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Message } from '../../../../core/models/message.model';

@Component({
  selector: 'app-message-bubble',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="flex items-end gap-1 animate-fade-in group relative"
      [ngClass]="{'justify-end': isMine, 'justify-start': !isMine}"
    >
      <!-- Avatar for received messages -->
      <img 
        *ngIf="!isMine"
        [src]="message.senderAvatar || getDefaultAvatar()"
        [alt]="message.senderName"
        class="w-8 h-8 rounded-full flex-shrink-0 ring-2 ring-[#2a3942]"
      >
      
      <div class="max-w-[75%] md:max-w-[65%] relative">
        <!-- Sender name for received messages in group chats -->
        <p *ngIf="!isMine && showSenderName" class="text-xs text-[#aebac1] mb-1 ml-2">
          {{ message.senderName }}
        </p>
        
        <!-- Message bubble -->
        <div 
          class="rounded-lg px-3 py-2 break-words shadow-md relative message-bubble"
          [ngClass]="{
            'bg-[#005c4b] text-white rounded-br-none': isMine,
            'bg-[#202c33] text-white rounded-bl-none': !isMine
          }"
        >
          <!-- Message tail -->
          <div 
            class="absolute bottom-0 w-0 h-0"
            [ngClass]="{
              'right-[-8px] border-l-[8px] border-l-[#005c4b] border-b-[8px] border-b-transparent': isMine,
              'left-[-8px] border-r-[8px] border-r-[#202c33] border-b-[8px] border-b-transparent': !isMine
            }"
          ></div>
          
          <!-- Text content -->
          <p class="whitespace-pre-wrap text-[15px] leading-relaxed font-medium">{{ message.content }}</p>
          
          <!-- File/Image preview -->
          <div *ngIf="message.fileUrl" class="mt-2 -mx-1">
            <img 
              *ngIf="message.messageType === 'IMAGE'"
              [src]="message.fileUrl"
              [alt]="message.fileName || 'Image'"
              class="max-w-full rounded-lg cursor-pointer hover:opacity-90 transition-opacity"
              (click)="openImage(message.fileUrl)"
            >
            
            <a 
              *ngIf="message.messageType === 'FILE'"
              [href]="message.fileUrl"
              target="_blank"
              class="flex items-center gap-2 p-3 bg-black bg-opacity-20 rounded-lg hover:bg-opacity-30 transition-colors"
            >
              <div class="p-2 bg-[#00a884] rounded-full">
                <svg class="w-5 h-5 text-[#111b21]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium truncate">{{ message.fileName }}</p>
                <p class="text-xs opacity-70">{{ formatFileSize(message.fileSize) }}</p>
              </div>
            </a>
          </div>
          
          <!-- Timestamp and status -->
          <div class="flex items-center justify-end gap-1 mt-1">
            <span *ngIf="message.isEdited" class="text-[10px] opacity-60 mr-1">
              modifié
            </span>
            
            <span class="text-[11px] opacity-70">
              {{ formatTime(message.createdAt) }}
            </span>
            
            <!-- Read status for sent messages -->
            <span *ngIf="isMine" class="ml-1">
              <!-- Double check (read) -->
              <svg 
                *ngIf="message.readBy && message.readBy.length > 0"
                class="w-4 h-4 text-[#53bdeb]"
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M9 13l4 4L23 7"></path>
              </svg>
              
              <!-- Single check (delivered) -->
              <svg 
                *ngIf="!message.readBy || message.readBy.length === 0"
                class="w-4 h-4 opacity-60"
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"></path>
              </svg>
            </span>
          </div>
        </div>
        
        <!-- Quick Reactions (appears on hover) -->
        <div class="absolute -top-8 left-0 right-0 flex justify-center opacity-0 group-hover:opacity-100 transition-opacity">
          <div class="bg-[#202c33] rounded-full px-2 py-1 flex gap-1 shadow-lg border border-[#2a3942]">
            <button 
              *ngFor="let emoji of quickReactions"
              (click)="react.emit({ messageId: message.id, reaction: emoji })"
              class="text-lg hover:scale-125 transition-transform"
              [title]="'Réagir avec ' + emoji"
            >
              {{ emoji }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .animate-fade-in {
      animation: fadeIn 0.3s ease-out;
    }
    
    .message-bubble {
      position: relative;
      transition: all 0.2s ease;
    }
    
    .message-bubble:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  `]
})
export class MessageBubbleComponent {
  @Input() message!: Message;
  @Input() isMine: boolean = false;
  @Input() showSenderName: boolean = false; // For group chats
  @Output() react = new EventEmitter<{ messageId: number, reaction: string }>();

  quickReactions: string[] = ['❤️', '😂', '😮', '😢', '👍', '🙏'];

  getDefaultAvatar(): string {
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(this.message.senderName)}&background=00a884&color=111b21`;
  }

  formatTime(date: Date): string {
    const messageDate = new Date(date);
    return messageDate.toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  formatFileSize(bytes?: number): string {
    if (!bytes) return '';
    
    const kb = bytes / 1024;
    if (kb < 1024) {
      return `${kb.toFixed(1)} KB`;
    }
    
    const mb = kb / 1024;
    return `${mb.toFixed(1)} MB`;
  }

  openImage(url?: string): void {
    if (url) {
      window.open(url, '_blank');
    }
  }
}
