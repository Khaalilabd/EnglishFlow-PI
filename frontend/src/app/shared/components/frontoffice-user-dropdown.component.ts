import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AuthResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-frontoffice-user-dropdown',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="user-dropdown">
      <button
        (click)="toggleDropdown()"
        class="user-btn"
      >
        <img
          [src]="getProfilePhotoUrl(currentUser?.profilePhoto)"
          [alt]="currentUser?.firstName + ' ' + currentUser?.lastName"
          class="user-avatar"
        />
        <span class="user-name">{{ currentUser?.firstName }} {{ currentUser?.lastName }}</span>
        <svg class="dropdown-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
        </svg>
      </button>

      <div
        *ngIf="isOpen"
        class="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-lg py-2 z-50"
      >
        <div class="px-4 py-3 border-b border-gray-100">
          <p class="text-sm font-medium text-gray-900">{{ currentUser?.firstName }} {{ currentUser?.lastName }}</p>
          <p class="text-xs text-gray-500">{{ currentUser?.email }}</p>
        </div>
        
        <a
          [routerLink]="userPanelRoute"
          class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
          (click)="closeDropdown()"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          {{ userPanelLabel }}
        </a>
        
        <a
          routerLink="/settings"
          class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
          (click)="closeDropdown()"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          Settings
        </a>
        
        <button
          (click)="logout()"
          class="flex items-center gap-3 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
          </svg>
          Logout
        </button>
      </div>
    </div>
  `,
  styles: [`
    .user-dropdown {
      position: relative;
    }
    
    .user-btn {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 6px 16px 6px 6px;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      border-radius: 50px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .user-btn:hover {
      background: rgba(255, 255, 255, 0.2);
    }
    
    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      border: 2px solid rgba(255, 255, 255, 0.3);
      object-fit: cover;
    }
    
    .user-name {
      color: #fff;
      font-weight: 500;
      font-size: 14px;
    }
    
    .dropdown-icon {
      width: 16px;
      height: 16px;
      color: #fff;
    }
    
    @media (max-width: 768px) {
      .user-name {
        display: none;
      }
    }
  `]
})
export class FrontofficeUserDropdownComponent implements OnInit {
  isOpen = false;
  currentUser: AuthResponse | null = null;
  
  constructor(
    public authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  ngOnInit() {
    // Listen for profile photo updates
    window.addEventListener('profilePhotoUpdated', (event: any) => {
      if (this.currentUser) {
        this.currentUser = {
          ...this.currentUser,
          profilePhoto: event.detail.profilePhoto
        };
      }
    });
  }

  getProfilePhotoUrl(photoUrl: string | null | undefined): string {
    if (!photoUrl) {
      return this.getDefaultAvatar();
    }
    
    // Si l'URL commence par http, la retourner telle quelle
    if (photoUrl.startsWith('http')) {
      return photoUrl;
    }
    
    // Sinon, ajouter le préfixe du backend
    return `http://localhost:8081${photoUrl}`;
  }

  getDefaultAvatar(): string {
    const name = `${this.currentUser?.firstName || 'User'}+${this.currentUser?.lastName || 'Name'}`;
    return `https://ui-avatars.com/api/?name=${name}&background=F6BD60&color=fff&size=128`;
  }

  get userPanelLabel(): string {
    if (this.currentUser?.role === 'ADMIN') {
      return 'Admin Dashboard';
    } else if (this.currentUser?.role === 'TUTOR') {
      return 'Tutor Panel';
    } else if (this.currentUser?.role === 'ACADEMIC_OFFICE_AFFAIR') {
      return 'Academic Support Panel';
    } else if (this.currentUser?.role === 'STUDENT') {
      return 'Student Panel';
    }
    return 'Student Panel'; // Default pour les autres cas
  }

  get userPanelRoute(): string {
    if (this.currentUser?.role === 'ADMIN') {
      return '/dashboard';
    } else if (this.currentUser?.role === 'TUTOR') {
      return '/tutor-panel';
    } else if (this.currentUser?.role === 'ACADEMIC_OFFICE_AFFAIR') {
      return '/dashboard';
    } else if (this.currentUser?.role === 'STUDENT') {
      return '/user-panel';
    }
    return '/user-panel'; // Default pour les autres cas
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  closeDropdown() {
    this.isOpen = false;
  }

  logout() {
    this.authService.logout();
    this.closeDropdown();
    this.router.navigate(['/']);
  }
}
