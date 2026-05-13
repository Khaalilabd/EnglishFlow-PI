import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, ChildrenOutletContexts, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { ToastComponent } from './shared/components/toast/toast.component';
import { NotificationToastComponent } from './shared/components/notification-toast/notification-toast.component';
import { EnglishPlacementTestComponent } from './auth/english-placement-test/english-placement-test.component';
import { slideAnimation } from './auth/auth-animations';
import { PlacementTestService } from './core/services/placement-test.service';
import { AuthService } from './core/services/auth.service';
import { ActivityTrackerService } from './services/activity-tracker.service';

declare var $: any;

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet, 
    ToastComponent, 
    NotificationToastComponent,
    EnglishPlacementTestComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  animations: [slideAnimation]
})
export class AppComponent implements OnInit {
  title = 'Jungle in English';
  showPlacementTest = false;
  private hasCheckedPlacementTest = false; // Flag to prevent multiple checks
  private lastCheckedRoute = ''; // Track which route was last checked

  constructor(
    private contexts: ChildrenOutletContexts,
    private placementTestService: PlacementTestService,
    private authService: AuthService,
    private router: Router,
    private activityTracker: ActivityTrackerService // Initialise le tracker automatiquement
  ) {}

  ngOnInit(): void {
    // Subscribe to placement test visibility
    this.placementTestService.showTest$.subscribe(show => {
      this.showPlacementTest = show;
    });

    // Listen to auth state changes to check placement test when user logs in
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        console.log('🔍 User state changed:', {
          englishLevel: user.englishLevel,
          role: user.role,
          hasChecked: this.hasCheckedPlacementTest
        });
        
        // Reset the flag when user data changes (e.g., after login or englishLevel update)
        // This allows re-checking if needed
        if (!this.hasCheckedPlacementTest) {
          console.log('🔍 Scheduling placement test check after user state change');
          setTimeout(() => {
            this.checkPlacementTest();
          }, 800);
        }
      }
    });

    // Check on navigation to student panel
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const isStudentRoute = event.url.startsWith('/user-panel');
      
      console.log('🚀 Navigation event:', {
        url: event.url,
        isStudentRoute: isStudentRoute,
        hasChecked: this.hasCheckedPlacementTest,
        lastCheckedRoute: this.lastCheckedRoute
      });
      
      // If navigating to student panel and haven't checked for this route yet
      if (isStudentRoute && (this.lastCheckedRoute !== event.url || !this.hasCheckedPlacementTest)) {
        console.log('🚀 Navigated to student panel, checking placement test');
        this.lastCheckedRoute = event.url;
        // Wait for user data to be fully loaded
        setTimeout(() => {
          this.checkPlacementTest();
        }, 500);
      }
      
      // Reset flag when navigating away from student panel
      if (!isStudentRoute && this.hasCheckedPlacementTest) {
        console.log('🚀 Navigated away from student panel, resetting check flag');
        this.hasCheckedPlacementTest = false;
        this.lastCheckedRoute = '';
      }
    });
  }

  private checkPlacementTest(): void {
    // Prevent multiple checks in the same session
    if (this.hasCheckedPlacementTest) {
      console.log('⏭️ Already checked placement test in this session');
      return;
    }

    // Get user from localStorage directly to avoid timing issues
    const storedUser = localStorage.getItem('currentUser');
    if (!storedUser) {
      console.log('⏭️ No user in localStorage, skipping test');
      return;
    }

    const user = JSON.parse(storedUser);
    const isStudentRoute = this.router.url.startsWith('/user-panel');
    
    console.log('🔎 Checking placement test conditions:', {
      hasUser: !!user,
      isStudentRoute: isStudentRoute,
      currentRoute: this.router.url,
      userRole: user?.role,
      englishLevel: user?.englishLevel,
      englishLevelType: typeof user?.englishLevel,
      englishLevelTrimmed: user?.englishLevel?.trim()
    });
    
    // Mark as checked to prevent future checks
    this.hasCheckedPlacementTest = true;
    
    // Check if user is a STUDENT without an English level
    const isStudent = user?.role === 'STUDENT';
    const hasNoEnglishLevel = !user?.englishLevel || 
                              (typeof user.englishLevel === 'string' && user.englishLevel.trim() === '');
    
    if (isStudent && hasNoEnglishLevel && isStudentRoute) {
      console.log('🎯 Student without English level detected - Triggering placement test');
      setTimeout(() => {
        this.placementTestService.triggerTest();
      }, 500);
    } else {
      console.log('⏭️ No placement test needed:', {
        isStudent,
        hasNoEnglishLevel,
        isStudentRoute
      });
      this.placementTestService.hideTest();
    }
  }

  onTestCompleted(level: string): void {
    console.log('✅ Placement test completed with level:', level);
    this.placementTestService.hideTest();
    
    // Reload user data to ensure englishLevel is updated
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      console.log('🔄 Reloading user data to verify English level...');
      this.authService.loadFreshUserData(currentUser.id);
    }
    
    // Keep the flag set so test doesn't show again in this session
    this.hasCheckedPlacementTest = true;
    this.lastCheckedRoute = this.router.url;
  }

  getRouteAnimationData() {
    return this.contexts.getContext('primary')?.route?.snapshot?.data?.['animation'];
  }
}
