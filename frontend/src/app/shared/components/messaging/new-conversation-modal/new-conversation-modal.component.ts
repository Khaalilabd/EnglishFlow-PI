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
  isGroupMode: boolean = false;
  showGroupForm: boolean = false;
  selectedUsers: User[] = [];
  groupName: string = '';
  groupTitle: string = '';
  groupDescription: string = '';
  groupPhotoPreview: string | null = null;
  groupPhotoFile: File | null = null;

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

  proceedToGroupForm(): void {
    this.showGroupForm = true;
  }

  createGroup(): void {
    if (!this.groupTitle.trim() || this.selectedUsers.length < 2) {
      return;
    }

    this.loading = true;
    this.error = '';

    const request = {
      participantIds: this.selectedUsers.map(u => u.id),
      type: ConversationType.GROUP,
      name: this.groupTitle
    };

    this.messagingService.createConversation(request).subscribe({
      next: (conversation) => {
        this.conversationCreated.emit(conversation.id);
        this.onClose();
      },
      error: (err) => {
        console.error('Error creating group conversation:', err);
        this.error = 'Impossible de créer la conversation de groupe';
        this.loading = false;
      }
    });
  }

  toggleUserSelection(user: User): void {
    user.selected = !user.selected;
    if (user.selected) {
      this.selectedUsers.push(user);
    } else {
      this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
    }
  }

  removeGroupPhoto(): void {
    this.groupPhotoPreview = null;
    this.groupPhotoFile = null;
  }

  onGroupPhotoSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.groupPhotoFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.groupPhotoPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeSelectedUser(user: User): void {
    this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
    user.selected = false;
  }

  backToUserSelection(): void {
    this.showGroupForm = false;
    this.groupTitle = '';
    this.groupDescription = '';
    this.groupPhotoPreview = null;
    this.groupPhotoFile = null;
  }

  toggleGroupMode(): void {
    this.isGroupMode = !this.isGroupMode;
    if (!this.isGroupMode) {
      this.selectedUsers = [];
      this.showGroupForm = false;
      this.filteredUsers.forEach(u => u.selected = false);
    }
  }

  getUserAvatar(user: User): string {
    if (user.profilePhotoUrl && !user.profilePhotoUrl.includes('ui-avatars.com')) {
      return `http://localhost:8088${user.profilePhotoUrl}`;
    }
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(user.firstName + ' ' + user.lastName)}&background=667eea&color=fff&bold=true&size=128`;
  }

  onClose(): void {
    this.close.emit();
  }
}
