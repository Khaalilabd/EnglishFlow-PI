import { Component, ElementRef, ViewChild } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ThemeToggleButtonComponent } from '../../components/common/theme-toggle/theme-toggle-button.component';
import { UserDropdownComponent } from '../../components/header/user-dropdown/user-dropdown.component';
import { AuthService } from '../../../core/services/auth.service';
import { PlacementTestService } from '../../../core/services/placement-test.service';

@Component({
  standalone: true,
  selector: 'app-header',
  imports: [
    CommonModule,
    RouterModule,
    ThemeToggleButtonComponent,
    UserDropdownComponent,
  ],
  templateUrl: './app-header.component.html',
})
export class AppHeaderComponent {
  isApplicationMenuOpen = false;
  readonly isMobileOpen$;

  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;

  constructor(
    public sidebarService: SidebarService,
    private authService: AuthService,
    private placementTestService: PlacementTestService,
    private router: Router
  ) {
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
  }

  shouldShowPlacementTestButton(): boolean {
    const currentUser = this.authService.currentUserValue;
    
    console.log('🎯 HEADER - shouldShowPlacementTestButton:', {
      hasUser: !!currentUser,
      role: currentUser?.role,
      englishLevel: currentUser?.englishLevel,
      englishLevelType: typeof currentUser?.englishLevel
    });
    
    // FORCE SHOW FOR TESTING - Always show for students
    if (currentUser?.role === 'STUDENT') {
      console.log('✅ HEADER - Showing button for STUDENT');
      return true;
    }
    
    console.log('❌ HEADER - Not showing button - not a student');
    return false;
  }

  navigateToPlacementTest(): void {
    console.log('🎯 HEADER - Navigating to placement test');
    this.router.navigate(['/user-panel/dashboard']).then(() => {
      setTimeout(() => {
        this.placementTestService.triggerTest();
      }, 300);
    });
  }

  handleToggle() {
    if (window.innerWidth >= 1280) {
      this.sidebarService.toggleExpanded();
    } else {
      this.sidebarService.toggleMobileOpen();
    }
  }

  toggleApplicationMenu() {
    this.isApplicationMenuOpen = !this.isApplicationMenuOpen;
  }

  ngAfterViewInit() {
    document.addEventListener('keydown', this.handleKeyDown);
  }

  ngOnDestroy() {
    document.removeEventListener('keydown', this.handleKeyDown);
  }

  handleKeyDown = (event: KeyboardEvent) => {
    if ((event.metaKey || event.ctrlKey) && event.key === 'k') {
      event.preventDefault();
      this.searchInput?.nativeElement.focus();
    }
  };
}
