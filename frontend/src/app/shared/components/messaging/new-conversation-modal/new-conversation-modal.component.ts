import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { MessagingService } from '../../../../core/services/messaging.service';
import { ConversationType } from '../../../../core/models/conversation.model';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  profilePhotoUrl?: string;
}

@Component({
  selector: 'app-new-conversation-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" (click)="onClose()">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4" (click)="$event.stopPropagation()">
        <!-- Header -->
        <div class="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 class="text-xl font-bold text-gray-900">💬 Nouvelle conversation</h2>
          <button 
            (click)="onClose()"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Body -->
        <div class="p-6">
          <!-- Search -->
          <div class="mb-4">
            <div class="relative">
              <input
                type="text"
                [(ngModel)]="searchQuery"
                (input)="onSearch()"
                placeholder="Rechercher un utilisateur..."
                class="w-full px-4 py-3 pl-10 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <svg class="w-5 h-5 text-gray-400 absolute left-3 top-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>

          <!-- Loading -->
          <div *ngIf="loading" class="text-center py-8">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            <p class="text-gray-500 mt-2">Chargement...</p>
          </div>

          <!-- Error -->
          <div *ngIf="error" class="bg-red-50 border border-red-200 rounded-xl p-4 mb-4">
            <p class="text-red-700 text-sm">{{ error }}</p>
          </div>

          <!-- User List -->
          <div *ngIf="!loading && !error" class="max-h-96 overflow-y-auto space-y-2">
            <div 
              *ngFor="let user of filteredUsers"
              (click)="selectUser(user)"
              class="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-xl cursor-pointer transition-colors border border-transparent hover:border-gray-200"
            >
              <div class="relative">
                <img 
                  [src]="user.profilePhotoUrl || 'https://ui-avatars.com/api/?name=' + user.firstName + '+' + user.lastName"
                  [alt]="user.firstName + ' ' + user.lastName"
                  class="w-12 h-12 rounded-full object-cover"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-gray-900 truncate">
                  {{ user.firstName }} {{ user.lastName }}
                </p>
                <p class="text-sm text-gray-500 truncate">{{ user.email }}</p>
              </div>
              <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>

            <div *ngIf="filteredUsers.length === 0" class="text-center py-8">
              <svg class="w-16 h-16 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
              <p class="text-gray-500">Aucun utilisateur trouvé</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class NewConversationModalComponent implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Output() conversationCreated = new EventEmitter<number>();

  users: User[] = [];
  filteredUsers: User[] = [];
  searchQuery: string = '';
  loading: boolean = false;
  error: string = '';

  constructor(
    private authService: AuthService,
    private messagingService: MessagingService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = '';
    
    // Récupérer tous les utilisateurs via AuthService
    this.authService.getAllUsers().subscribe({
      next: (users) => {
        const currentUserId = this.authService.currentUserValue?.id;
        // Exclure l'utilisateur actuel
        this.users = users.filter(u => u.id !== currentUserId);
        this.filteredUsers = this.users;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.error = 'Impossible de charger les utilisateurs';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    const query = this.searchQuery.toLowerCase().trim();
    if (!query) {
      this.filteredUsers = this.users;
      return;
    }

    this.filteredUsers = this.users.filter(user => 
      user.firstName.toLowerCase().includes(query) ||
      user.lastName.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
    );
  }

  selectUser(user: User): void {
    this.loading = true;
    this.error = '';

    // Créer une conversation avec cet utilisateur
    const request = {
      participantIds: [user.id],
      type: ConversationType.DIRECT
    };

    this.messagingService.createConversation(request).subscribe({
      next: (conversation) => {
        console.log('✅ Conversation created:', conversation);
        this.conversationCreated.emit(conversation.id);
        this.onClose();
      },
      error: (err) => {
        console.error('❌ Error creating conversation:', err);
        this.error = 'Impossible de créer la conversation';
        this.loading = false;
      }
    });
  }

  onClose(): void {
    this.close.emit();
  }
}
