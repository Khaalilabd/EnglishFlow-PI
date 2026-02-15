import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Message {
  id: number;
  sender: string;
  avatar: string;
  lastMessage: string;
  time: string;
  unread: number;
  online: boolean;
}

interface ChatMessage {
  id: number;
  text: string;
  time: string;
  isMine: boolean;
}

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="h-[calc(100vh-200px)] bg-white rounded-xl shadow-sm overflow-hidden">
      <div class="grid grid-cols-12 h-full">
        <!-- Contacts List -->
        <div class="col-span-12 md:col-span-4 border-r border-gray-200">
          <div class="p-4 border-b border-gray-200">
            <h2 class="text-xl font-bold text-gray-900">Messages</h2>
            <input 
              type="text" 
              placeholder="Search conversations..." 
              class="w-full mt-3 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F59E0B]"
            />
          </div>
          
          <div class="overflow-y-auto h-[calc(100%-120px)]">
            <div 
              *ngFor="let contact of contacts"
              (click)="selectContact(contact)"
              class="p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors"
              [ngClass]="{'bg-blue-50': selectedContact?.id === contact.id}"
            >
              <div class="flex items-center gap-3">
                <div class="relative">
                  <img [src]="contact.avatar" alt="{{ contact.sender }}" class="w-12 h-12 rounded-full">
                  <span 
                    *ngIf="contact.online"
                    class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-white rounded-full"
                  ></span>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center justify-between">
                    <h3 class="font-semibold text-gray-900 truncate">{{ contact.sender }}</h3>
                    <span class="text-xs text-gray-500">{{ contact.time }}</span>
                  </div>
                  <p class="text-sm text-gray-600 truncate">{{ contact.lastMessage }}</p>
                </div>
                <span 
                  *ngIf="contact.unread > 0"
                  class="px-2 py-1 bg-[#F59E0B] text-white text-xs rounded-full font-semibold"
                >
                  {{ contact.unread }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Chat Area -->
        <div class="col-span-12 md:col-span-8 flex flex-col">
          <div *ngIf="selectedContact" class="flex-1 flex flex-col">
            <!-- Chat Header -->
            <div class="p-4 border-b border-gray-200 flex items-center gap-3">
              <img [src]="selectedContact.avatar" alt="{{ selectedContact.sender }}" class="w-10 h-10 rounded-full">
              <div>
                <h3 class="font-semibold text-gray-900">{{ selectedContact.sender }}</h3>
                <p class="text-sm text-green-500" *ngIf="selectedContact.online">Online</p>
                <p class="text-sm text-gray-500" *ngIf="!selectedContact.online">Offline</p>
              </div>
            </div>

            <!-- Messages -->
            <div class="flex-1 overflow-y-auto p-4 space-y-4">
              <div 
                *ngFor="let msg of chatMessages"
                class="flex"
                [ngClass]="{'justify-end': msg.isMine}"
              >
                <div 
                  class="max-w-[70%] rounded-lg p-3"
                  [ngClass]="{
                    'bg-[#2D5757] text-white': msg.isMine,
                    'bg-gray-100 text-gray-900': !msg.isMine
                  }"
                >
                  <p>{{ msg.text }}</p>
                  <span class="text-xs opacity-70 mt-1 block">{{ msg.time }}</span>
                </div>
              </div>
            </div>

            <!-- Message Input -->
            <div class="p-4 border-t border-gray-200">
              <div class="flex gap-2">
                <input 
                  [(ngModel)]="newMessage"
                  (keyup.enter)="sendMessage()"
                  type="text" 
                  placeholder="Type your message..." 
                  class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-[#F59E0B]"
                />
                <button 
                  (click)="sendMessage()"
                  class="px-6 py-2 bg-[#F59E0B] text-white rounded-lg hover:bg-[#e5ac4f] transition-colors"
                >
                  Send
                </button>
              </div>
            </div>
          </div>

          <div *ngIf="!selectedContact" class="flex-1 flex items-center justify-center text-gray-400">
            <div class="text-center">
              <svg class="w-20 h-20 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
              </svg>
              <p class="text-lg">Select a conversation to start messaging</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MessagesComponent {
  contacts: Message[] = [
    {
      id: 1,
      sender: 'Sarah Johnson',
      avatar: 'https://i.pravatar.cc/100?img=1',
      lastMessage: 'Great progress on your last assignment!',
      time: '2m ago',
      unread: 2,
      online: true
    },
    {
      id: 2,
      sender: 'Michael Brown',
      avatar: 'https://i.pravatar.cc/100?img=2',
      lastMessage: 'Don\'t forget about tomorrow\'s class',
      time: '1h ago',
      unread: 0,
      online: true
    },
    {
      id: 3,
      sender: 'Emily Davis',
      avatar: 'https://i.pravatar.cc/100?img=3',
      lastMessage: 'Your presentation was excellent!',
      time: '3h ago',
      unread: 1,
      online: false
    }
  ];

  selectedContact: Message | null = null;
  newMessage: string = '';
  
  chatMessages: ChatMessage[] = [];

  selectContact(contact: Message) {
    this.selectedContact = contact;
    // Load chat messages for this contact
    this.chatMessages = [
      { id: 1, text: 'Hello! How can I help you today?', time: '10:30 AM', isMine: false },
      { id: 2, text: 'Hi! I have a question about the assignment', time: '10:32 AM', isMine: true },
      { id: 3, text: 'Sure, what would you like to know?', time: '10:33 AM', isMine: false },
    ];
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.chatMessages.push({
        id: this.chatMessages.length + 1,
        text: this.newMessage,
        time: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }),
        isMine: true
      });
      this.newMessage = '';
    }
  }
}
