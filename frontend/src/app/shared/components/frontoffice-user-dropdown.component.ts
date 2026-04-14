import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AuthResponse } from '../../core/models/user.model';
import { UserRoleBadgeComponent } from './user-role-badge/user-role-badge.component';
import { PlacementTestService } from '../../core/services/placement-test.service';

@Component({
  selector: 'app-frontoffice-user-dropdown',
  standalone: true,
  imports: [CommonModule, RouterModule, UserRoleBadgeComponent],
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
          <div class="flex items-center gap-2 mb-1">
            <p class="text-sm font-medium text-gray-900">{{ currentUser?.firstName }} {{ currentUser?.lastName }}</p>
            <app-user-role-badge [role]="currentUser?.role || ''"></app-user-role-badge>
            <!-- English Level Badge -->
            <span 
              *ngIf="currentUser?.role === 'STUDENT' && currentUser?.englishLevel"
              class="inline-flex items-center justify-center px-2.5 py-1 text-xs font-bold text-white bg-gradient-to-r from-green-500 to-green-600 rounded-full"
            >
              {{ currentUser?.englishLevel }}
            </span>
          </div>
          <p class="text-xs text-gray-500">{{ currentUser?.email }}</p>
        </div>
        
        <!-- NEW ACCOUNTS: Show "Take English Test" button if NO englishLevel -->
        <button
          *ngIf="currentUser?.role === 'STUDENT' && needsEnglishTest()"
          (click)="navigateToPlacementTest()"
          class="flex items-center gap-3 w-full px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 transition-all duration-200"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
          Take English Test 🎯
        </button>
        
        <!-- EXISTING ACCOUNTS: Show "Student Panel" if HAS englishLevel -->
        <a
          *ngIf="currentUser?.role === 'STUDENT' && hasEnglishLevel()"
          [routerLink]="userPanelRoute"
          class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
          (click)="closeDropdown()"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          Student Panel
        </a>
        
        <!-- NON-STUDENT USERS: Show their respective panel -->
        <a
          *ngIf="currentUser?.role !== 'STUDENT'"
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
      gap: 16px;
      padding: 6px 24px 6px 6px;
      background: transparent;
      border: none;
      border-radius: 50px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .user-btn:hover {
      background: rgba(246, 189, 96, 0.15);
    }
    
    .user-avatar {
      width: 52px;
      height: 52px;
      min-width: 52px;
      min-height: 52px;
      border-radius: 50%;
      border: 3px solid #F6BD60;
      object-fit: cover;
      box-shadow: 0 4px 16px rgba(246, 189, 96, 0.6);
      transition: all 0.3s ease;
      background: #fff;
      flex-shrink: 0;
    }
    
    .user-btn:hover .user-avatar {
      border-color: #f5b04a;
      box-shadow: 0 6px 20px rgba(246, 189, 96, 0.8);
      transform: scale(1.05);
    }
    
    .user-name {
      color: #fff;
      font-weight: 600;
      font-size: 16px;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
    }
    
    .dropdown-icon {
      width: 20px;
      height: 20px;
      color: #F6BD60;
      transition: transform 0.3s ease;
      flex-shrink: 0;
    }
    
    .user-btn:hover .dropdown-icon {
      transform: translateY(2px);
    }
    
    /* English Level Badge with Green Glow Animation */
    .english-level-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 4px 10px;
      font-size: 11px;
      font-weight: 700;
      color: #fff;
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      border-radius: 12px;
      box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
      animation: pulse-glow 2s infinite;
      letter-spacing: 0.5px;
      text-transform: uppercase;
    }
    
    @keyframes pulse-glow {
      0% {
        box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
      }
      50% {
        box-shadow: 0 0 0 8px rgba(16, 185, 129, 0);
      }
      100% {
        box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
      }
    }
    
    @media (max-width: 768px) {
      .user-name {
        display: none;
      }
      
      .user-avatar {
        width: 48px;
        height: 48px;
        min-width: 48px;
        min-height: 48px;
      }
    }
  `]
})
export class FrontofficeUserDropdownComponent implements OnInit {
  isOpen = false;
  currentUser: AuthResponse | null = null;
  
  constructor(
    public authService: AuthService,
    private router: Router,
    private placementTestService: PlacementTestService
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
    } else if (this.currentUser?.role === 'SPONSOR') {
      return 'Sponsor Panel';
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
    } else if (this.currentUser?.role === 'SPONSOR') {
      return '/sponsor-panel/dashboard';
    } else if (this.currentUser?.role === 'STUDENT') {
      return '/user-panel';
    }
    return '/user-panel'; // Default pour les autres cas
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
    
    // When opening dropdown, force check the latest user data from localStorage
    if (this.isOpen) {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        const user = JSON.parse(storedUser);
        this.currentUser = user;
        console.log('🔍 Frontoffice Dropdown opened - Refreshed user data, englishLevel:', user.englishLevel);
      }
    }
  }

  closeDropdown() {
    this.isOpen = false;
  }

  /**
   * Determines if user needs to take the English placement test
   * Returns TRUE if user has NO valid English level (needs test)
   * Returns FALSE if user has a valid English level (already took test)
   */
  needsEnglishTest(): boolean {
    const englishLevel = this.currentUser?.englishLevel;
    
    // Check if englishLevel is missing, null, undefined, empty, or whitespace
    const hasNoLevel = !englishLevel || 
                       (typeof englishLevel === 'string' && englishLevel.trim() === '');
    
    console.log('🔍 Frontoffice needsEnglishTest check:', {
      englishLevel: englishLevel,
      hasNoLevel: hasNoLevel
    });
    
    return !!hasNoLevel;
  }

  /**
   * Determines if user has already completed the English test
   * Returns TRUE if user has a valid English level
   * Returns FALSE if user needs to take the test
   */
  hasEnglishLevel(): boolean {
    const englishLevel = this.currentUser?.englishLevel;
    
    // Check if englishLevel exists and is not empty/whitespace
    const hasLevel = !!(englishLevel && 
                       typeof englishLevel === 'string' && 
                       englishLevel.trim() !== '');
    
    console.log('🔍 Frontoffice hasEnglishLevel check:', {
      englishLevel: englishLevel,
      hasLevel: hasLevel
    });
    
    return hasLevel;
  }

  navigateToPlacementTest(): void {
    this.closeDropdown();
    console.log('🎯 Triggering placement test from frontoffice dropdown');
    
    // Navigate to student panel first
    this.router.navigate(['/user-panel/dashboard']).then(() => {
      // Then trigger the test after a short delay
      setTimeout(() => {
        this.placementTestService.triggerTest();
      }, 300);
    });
  }

  logout() {
    this.authService.logout().subscribe({
      complete: () => {
        this.closeDropdown();
        this.router.navigate(['/']);
      }
    });
  }
}
