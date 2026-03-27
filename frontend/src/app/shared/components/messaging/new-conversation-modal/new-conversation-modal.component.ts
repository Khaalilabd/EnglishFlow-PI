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
  selected?: boolean;
}

@Component({
  selector: 'app-new-conversation-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './new-conversation-modal.component.html',
  styleUrls: ['./new-conversation-modal.component.scss']
})
export class NewConversationModalComponent implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Output() conversationCreated = new EventEmitter<number>();

  users: User[] = [];
  filteredUsers: User[] = [];
  searchQuery: string = '';
  loading: boolean = false;
  error: string = '';
  
  // Nouvelles propriétés pour les groupes
  isGroupMode: boolean = false;
  selectedUsers: User[] = [];
  groupTitle: string = '';
  groupDescription: string = '';
  showGroupForm: boolean = false;
  groupPhoto: File | null = null;
  groupPhotoPreview: string | null = null;

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
    
    this.authService.getAllUsers().subscribe({
      next: (users) => {
        const currentUserId = this.authService.currentUserValue?.id;
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
    if (this.isGroupMode) {
      // Mode groupe : sélectionner/désélectionner l'utilisateur
      user.selected = !user.selected;
      if (user.selected) {
        this.selectedUsers.push(user);
      } else {
        this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
      }
    } else {
      // Mode direct : créer immédiatement la conversation
      this.createDirectConversation(user);
    }
  }

  createDirectConversation(user: User): void {
    this.loading = true;
    this.error = '';

    const request = {
      participantIds: [user.id],
      type: ConversationType.DIRECT
    };

    this.messagingService.createConversation(request).subscribe({
      next: (conversation) => {
        this.conversationCreated.emit(conversation.id);
        this.onClose();
      },
      error: (err) => {
        console.error('Error creating conversation:', err);
        this.error = 'Impossible de créer la conversation';
        this.loading = false;
      }
    });
  }

  toggleGroupMode(): void {
    this.isGroupMode = !this.isGroupMode;
    this.selectedUsers = [];
    this.users.forEach(u => u.selected = false);
    this.showGroupForm = false;
    this.groupTitle = '';
    this.groupDescription = '';
    this.groupPhoto = null;
    this.groupPhotoPreview = null;
  }

  proceedToGroupForm(): void {
    if (this.selectedUsers.length < 2) {
      this.error = 'Sélectionnez au moins 2 participants pour créer un groupe';
      return;
    }
    this.showGroupForm = true;
  }

  backToUserSelection(): void {
    this.showGroupForm = false;
  }

  createGroup(): void {
    if (!this.groupTitle.trim()) {
      this.error = 'Le titre du groupe est obligatoire';
      return;
    }

    if (this.selectedUsers.length < 2) {
      this.error = 'Sélectionnez au moins 2 participants';
      return;
    }

    this.loading = true;
    this.error = '';

    // Si une photo est sélectionnée, l'uploader d'abord
    if (this.groupPhoto) {
      const formData = new FormData();
      formData.append('file', this.groupPhoto);

      this.messagingService.uploadGroupPhoto(formData).subscribe({
        next: (response: {groupPhoto: string}) => {
          // Photo uploadée, créer le groupe avec l'URL de la photo
          this.createGroupWithPhoto(response.groupPhoto);
        },
        error: (err: any) => {
          console.error('Error uploading group photo:', err);
          this.error = 'Impossible d\'uploader la photo du groupe';
          this.loading = false;
        }
      });
    } else {
      // Pas de photo, créer le groupe directement
      this.createGroupWithPhoto(undefined);
    }
  }

  private createGroupWithPhoto(groupPhotoUrl?: string): void {
    const request = {
      participantIds: this.selectedUsers.map(u => u.id),
      type: ConversationType.GROUP,
      title: this.groupTitle.trim(),
      description: this.groupDescription.trim() || undefined,
      groupPhoto: groupPhotoUrl
    };

    this.messagingService.createConversation(request).subscribe({
      next: (conversation) => {
        this.conversationCreated.emit(conversation.id);
        this.onClose();
      },
      error: (err) => {
        console.error('Error creating group:', err);
        this.error = 'Impossible de créer le groupe';
        this.loading = false;
      }
    });
  }

  removeSelectedUser(user: User): void {
    user.selected = false;
    this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
  }

  onGroupPhotoSelected(event: any): void {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.groupPhoto = file;
      
      // Créer une preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.groupPhotoPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    } else {
      this.error = 'Veuillez sélectionner une image valide';
    }
  }

  removeGroupPhoto(): void {
    this.groupPhoto = null;
    this.groupPhotoPreview = null;
  }

  getUserAvatar(user: User): string {
    if (user.profilePhotoUrl) {
      // Si l'URL commence par http, c'est une URL complète (Google, etc.)
      if (user.profilePhotoUrl.startsWith('http')) {
        return user.profilePhotoUrl;
      }
      // Sinon, c'est un chemin relatif du serveur auth-service (port 8081)
      if (!user.profilePhotoUrl.includes('ui-avatars.com')) {
        return `http://localhost:8081${user.profilePhotoUrl}`;
      }
    }
    // Fallback sur ui-avatars
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(user.firstName + ' ' + user.lastName)}&background=667eea&color=fff&bold=true&size=128`;
  }

  onClose(): void {
    this.close.emit();
  }
}
