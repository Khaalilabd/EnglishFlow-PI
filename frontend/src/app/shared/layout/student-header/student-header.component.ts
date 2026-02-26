import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/user.model';

@Component({
  standalone: true,
  selector: 'app-student-header',
  imports: [CommonModule, RouterModule],
  template: `
    <header 
      class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between shadow-sm transition-all duration-300">
      
      <!-- Left: Mobile Toggle + Search -->
      <div class="flex items-center flex-1 max-w-2xl gap-4">
        <!-- Hamburger Toggle for Mobile -->
        <button
          (click)="toggleSidebar()"
          class="block rounded-xl p-2 hover:bg-[#F7EDE2] xl:hidden transition-colors"
        >
          <svg class="h-6 w-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
          </svg>
        </button>
        
        <!-- Search Bar -->
        <div class="relative flex-1">
          <i class="fas fa-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
          <input 
            type="text" 
            placeholder="Search courses, lessons, assignments..."
            class="w-full pl-12 pr-4 py-2.5 bg-[#F7EDE2]/30 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#F6BD60] focus:border-transparent transition-all">
        </div>
      </div>

      <!-- Right: Actions -->
      <div class="flex items-center space-x-4 ml-6">
        <!-- TEST Menu -->
        <a 
          routerLink="/user-panel/courses"
          class="px-4 py-2 bg-yellow-500 hover:bg-yellow-600 text-white font-semibold rounded-lg transition-colors shadow-sm">
          TEST
        </a>

        <!-- Notifications -->
        <button class="relative p-2 hover:bg-[#F7EDE2] rounded-xl transition-colors">
          <i class="fas fa-bell text-gray-600 text-xl"></i>
          <span class="absolute top-1 right-1 w-2 h-2 bg-[#F6BD60] rounded-full animate-pulse"></span>
        </button>

        <!-- Messages -->
        <button class="relative p-2 hover:bg-[#F7EDE2] rounded-xl transition-colors">
          <i class="fas fa-envelope text-gray-600 text-xl"></i>
          <span *ngIf="unreadMessages > 0" class="absolute top-0 right-0 bg-[#C84630] text-white text-xs font-bold px-1.5 py-0.5 rounded-full">{{unreadMessages}}</span>
        </button>

        <!-- User Menu -->
        <div class="relative">
          <button 
            (click)="toggleUserMenu()"
            class="flex items-center space-x-3 pl-4 border-l border-gray-200 hover:bg-[#F7EDE2] rounded-lg px-3 py-2 transition-colors">
            <img 
              [src]="getProfilePhotoUrl(currentUser?.profilePhoto)"
              [alt]="currentUser?.firstName + ' ' + currentUser?.lastName"
              class="w-10 h-10 rounded-full border-2 border-[#F6BD60] shadow-sm object-cover">
            <div class="hidden md:block text-left">
              <p class="text-sm font-semibold text-gray-800">{{currentUser?.firstName}} {{currentUser?.lastName}}</p>
              <p class="text-xs text-[#2D5757]">{{currentUser?.role === 'STUDENT' ? 'Learner' : currentUser?.role}}</p>
            </div>
            <i class="fas fa-chevron-down text-gray-600 text-sm transition-transform" [class.rotate-180]="userMenuOpen"></i>
          </button>

          <!-- Dropdown Menu -->
          <div 
            *ngIf="userMenuOpen"
            class="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-xl border border-gray-200 py-2 z-50 animate-fadeIn">
            
            <!-- User Info -->
            <div class="px-4 py-3 border-b border-gray-100">
              <p class="text-sm font-semibold text-gray-800">{{currentUser?.firstName}} {{currentUser?.lastName}}</p>
              <p class="text-xs text-gray-500">{{currentUser?.email}}</p>
            </div>

            <!-- Menu Items -->
            <a 
              routerLink="/user-panel/settings"
              (click)="closeUserMenu()"
              class="flex items-center px-4 py-3 hover:bg-[#F7EDE2] transition-colors">
              <i class="fas fa-user-cog text-[#2D5757] w-5"></i>
              <span class="ml-3 text-sm text-gray-700">Settings Profile</span>
            </a>

            <a 
              routerLink="/user-panel/subscription"
              (click)="closeUserMenu()"
              class="flex items-center px-4 py-3 hover:bg-[#F7EDE2] transition-colors">
              <i class="fas fa-credit-card text-[#2D5757] w-5"></i>
              <span class="ml-3 text-sm text-gray-700">My Subscription</span>
            </a>

            <a 
              routerLink="/user-panel/support"
              (click)="closeUserMenu()"
              class="flex items-center px-4 py-3 hover:bg-[#F7EDE2] transition-colors">
              <i class="fas fa-life-ring text-[#2D5757] w-5"></i>
              <span class="ml-3 text-sm text-gray-700">Help & Support</span>
            </a>

            <div class="border-t border-gray-100 mt-2"></div>

            <button 
              (click)="logout()"
              class="w-full flex items-center px-4 py-3 hover:bg-red-50 transition-colors text-left">
              <i class="fas fa-sign-out-alt text-red-500 w-5"></i>
              <span class="ml-3 text-sm text-red-500 font-medium">Logout</span>
            </button>
          </div>
        </div>
      </div>
    </header>
  `,
  styles: [`
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .animate-fadeIn {
      animation: fadeIn 0.2s ease-out;
    }
  `]
})
export class StudentHeaderComponent {
  userMenuOpen = false;
  unreadMessages = 5;
  currentUser$;
  currentUser: AuthResponse | null = null;

  constructor(
    private sidebarService: SidebarService,
    private authService: AuthService
  ) {
    this.currentUser$ = this.authService.currentUser$;
    
    // Subscribe to currentUser changes
    this.authService.currentUser$.subscribe(user => {
      console.log('Student Header - User updated:', user);
      this.currentUser = user;
    });

    // Listen for profile photo updates
    window.addEventListener('profilePhotoUpdated', (event: any) => {
      console.log('Student Header - Profile photo updated event:', event.detail);
      if (this.currentUser) {
        this.currentUser = {
          ...this.currentUser,
          profilePhoto: event.detail.profilePhoto
        };
        // Force update in authService
        this.authService.updateCurrentUser(this.currentUser);
      }
    });
  }

  getProfilePhotoUrl(photoUrl: string | null | undefined): string {
    if (!photoUrl) {
      const name = `${this.currentUser?.firstName || 'User'}+${this.currentUser?.lastName || 'Name'}`;
      return `https://ui-avatars.com/api/?name=${name}&background=F6BD60&color=fff&size=128`;
    }
    
    // Si l'URL commence par http, la retourner telle quelle
    if (photoUrl.startsWith('http')) {
      return photoUrl;
    }
    
    // Sinon, ajouter le préfixe du backend
    return `http://localhost:8081${photoUrl}`;
  }

  toggleSidebar() {
    this.sidebarService.toggleMobileOpen();
  }

  toggleUserMenu() {
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeUserMenu() {
    this.userMenuOpen = false;
  }

  logout() {
    this.authService.logout().subscribe({
      complete: () => {
        window.location.href = '/';
      }
    });
  }
}
