import { Component } from '@angular/core';
import { DropdownComponent } from '../../ui/dropdown/dropdown.component';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { DropdownItemTwoComponent } from '../../ui/dropdown/dropdown-item/dropdown-item.component-two';
import { AuthService } from '../../../../core/services/auth.service';
import { AuthResponse } from '../../../../core/models/user.model';
import { UserRoleBadgeComponent } from '../../user-role-badge/user-role-badge.component';
import { PlacementTestService } from '../../../../core/services/placement-test.service';

@Component({
  standalone: true,
  selector: 'app-user-dropdown',
  templateUrl: './user-dropdown.component.html',
  imports:[CommonModule, RouterModule, DropdownComponent, DropdownItemTwoComponent, UserRoleBadgeComponent],
  styles: [`
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
  `]
})
export class UserDropdownComponent {
  isOpen = false;
  currentUser: AuthResponse | null = null;

  constructor(
    private authService: AuthService,
    private placementTestService: PlacementTestService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
    // Subscribe to user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      console.log('🔄 UserDropdown - User updated:', {
        englishLevel: user?.englishLevel,
        englishLevelType: typeof user?.englishLevel,
        role: user?.role
      });
    });
  }

  getProfilePhotoUrl(): string {
    if (this.currentUser?.profilePhoto) {
      // If URL starts with http, return as is (external URL like Google profile)
      if (this.currentUser.profilePhoto.startsWith('http')) {
        return this.currentUser.profilePhoto;
      }
      // Otherwise, add backend prefix for local uploads
      return `http://localhost:8081${this.currentUser.profilePhoto}`;
    }
    // Default avatar if no photo
    const name = `${this.currentUser?.firstName || 'User'}+${this.currentUser?.lastName || 'Name'}`;
    return `https://ui-avatars.com/api/?name=${name}&background=F6BD60&color=fff&size=128`;
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
    
    // When opening dropdown, force reload from backend to get latest englishLevel
    if (this.isOpen) {
      const currentUser = this.authService.currentUserValue;
      if (currentUser?.id) {
        console.log('🔄 Dropdown opened - Force reloading user data from backend...');
        this.authService.loadFreshUserData(currentUser.id);
        
        // Also check localStorage
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
          const user = JSON.parse(storedUser);
          this.currentUser = user;
          console.log('📊 Current englishLevel in localStorage:', user.englishLevel);
        }
      }
    }
  }

  closeDropdown() {
    this.isOpen = false;
  }

  logout() {
    this.authService.logout().subscribe({
      complete: () => {
        window.location.href = '/';
      }
    });
  }

  getLevelDescription(): string {
    const levelName = this.currentUser?.gamificationLevel?.assessmentLevelName;
    if (!levelName) return 'Level';
    const parts = levelName.split(' - ');
    return parts.length > 1 ? parts[1] : 'Level';
  }

  getSettingsRoute(): string {
    const role = this.currentUser?.role;
    switch (role) {
      case 'STUDENT':
        return '/user-panel/settings';
      case 'TUTOR':
      case 'TEACHER':
        return '/tutor-panel/settings';
      case 'SPONSOR':
        return '/sponsor-panel/company-profile';
      case 'ADMIN':
      case 'ACADEMIC_OFFICE_AFFAIR':
        return '/dashboard/settings';
      default:
        return '/dashboard/settings';
    }
  }

  getProfileRoute(): string {
    const role = this.currentUser?.role;
    switch (role) {
      case 'STUDENT':
        return '/user-panel/settings';
      case 'TUTOR':
      case 'TEACHER':
        return '/tutor-panel/profile';
      case 'SPONSOR':
        return '/sponsor-panel/company-profile';
      case 'ADMIN':
      case 'ACADEMIC_OFFICE_AFFAIR':
        return '/dashboard/profile';
      default:
        return '/dashboard/profile';
    }
  }

  navigateToPlacementTest(): void {
    this.closeDropdown();
    console.log('🎯 Triggering placement test from user dropdown');
    
    // Navigate to dashboard first, then trigger the test
    this.router.navigate(['/user-panel/dashboard']).then(() => {
      setTimeout(() => {
        this.placementTestService.triggerTest();
      }, 300);
    });
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
    
    console.log('🔍 needsEnglishTest check:', {
      englishLevel: englishLevel,
      type: typeof englishLevel,
      trimmed: englishLevel?.trim(),
      hasNoLevel: hasNoLevel,
      needsTest: hasNoLevel
    });
    
    return !!hasNoLevel; // Double negation to ensure boolean
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
    
    console.log('🔍 hasEnglishLevel check:', {
      englishLevel: englishLevel,
      hasLevel: hasLevel
    });
    
    return hasLevel;
  }
}