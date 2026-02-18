import { CommonModule } from '@angular/common';
import { Component, ChangeDetectorRef } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { combineLatest, forkJoin, Subscription } from 'rxjs';
import { ClubService } from '../../../core/services/club.service';
import { MemberService } from '../../../core/services/member.service';
import { Club } from '../../../core/models/club.model';
import { AuthService } from '../../../core/services/auth.service';

type NavItem = {
  name: string;
  icon: string;
  path?: string;
  badge?: string;
  badgeColor?: string;
  subItems?: { name: string; path: string; badge?: string; isPresident?: boolean }[];
};

@Component({
  standalone: true,
  selector: 'app-student-sidebar',
  imports: [CommonModule, RouterModule],
  templateUrl: './student-sidebar.component.html',
})
export class StudentSidebarComponent {
  // Main nav items for students
  navItems: NavItem[] = [
    {
      icon: 'fas fa-home',
      name: "Dashboard",
      path: "/user-panel/dashboard",
    },
    {
      icon: 'fas fa-book',
      name: "My Courses",
      path: "/user-panel/courses",
      badge: "3",
      badgeColor: "bg-[#F6BD60]"
    },
    {
      icon: 'fas fa-calendar-alt',
      name: "My Schedule",
      path: "/user-panel/schedule",
    },
    {
      icon: 'fas fa-clipboard-list',
      name: "Assignments",
      path: "/user-panel/assignments",
      badge: "2",
      badgeColor: "bg-orange-500"
    },
    {
      icon: 'fas fa-question-circle',
      name: "Quizzes",
      path: "/user-panel/quizzes",
    },
    {
      icon: 'fas fa-book-open',
      name: "Ebooks",
      path: "/user-panel/ebooks",
    },
    {
      icon: 'fas fa-users',
      name: "Clubs",
      path: "/user-panel/clubs",
      subItems: [
        // User's clubs will be added by loadUserClubs()
        { name: "Loading...", path: "/user-panel/clubs" }
      ]
    },
    {
      icon: 'fas fa-chart-line',
      name: "My Progress",
      path: "/user-panel/progress",
    },
    {
      icon: 'fas fa-comments',
      name: "Forum",
      path: "/user-panel/forum",
    },
    {
      icon: 'fas fa-envelope',
      name: "Messages",
      path: "/user-panel/messages",
      badge: "5",
      badgeColor: "bg-red-500"
    },
  ];

  // Support & Settings
  othersItems: NavItem[] = [
    {
      icon: 'fas fa-credit-card',
      name: "My Subscription",
      path: "/user-panel/subscription",
    },
    {
      icon: 'fas fa-life-ring',
      name: "Help & Support",
      path: "/user-panel/support",
    },
    {
      icon: 'fas fa-exclamation-circle',
      name: "Report Issue",
      path: "/user-panel/complaints",
    },
    {
      icon: 'fas fa-cog',
      name: "Settings",
      path: "/user-panel/settings",
    },
  ];

  openSubmenu: string | null | number = null;
  subMenuHeights: { [key: string]: number } = {};

  readonly isExpanded$;
  readonly isMobileOpen$;
  readonly isHovered$;

  private subscription: Subscription = new Subscription();
  userClubs: Club[] = [];
  clubRoles: { [clubId: number]: string } = {};

  constructor(
    public sidebarService: SidebarService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private clubService: ClubService,
    private memberService: MemberService,
    private authService: AuthService
  ) {
    this.isExpanded$ = this.sidebarService.isExpanded$;
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
    this.isHovered$ = this.sidebarService.isHovered$;
  }

  ngOnInit() {
    this.subscription.add(
      this.router.events.subscribe(event => {
        if (event instanceof NavigationEnd) {
          this.setActiveMenuFromRoute(this.router.url);
        }
      })
    );

    this.subscription.add(
      combineLatest([this.isExpanded$, this.isMobileOpen$, this.isHovered$]).subscribe(
        ([isExpanded, isMobileOpen, isHovered]) => {
          if (!isExpanded && !isMobileOpen && !isHovered) {
            this.cdr.detectChanges();
          }
        }
      )
    );

    // Listen for club membership changes
    this.subscription.add(
      this.clubService.clubMembershipChanged$.subscribe(() => {
        console.log('🔄 Club membership changed, reloading clubs in sidebar...');
        this.loadUserClubs();
      })
    );

    this.setActiveMenuFromRoute(this.router.url);
    this.loadUserClubs();
  }

  loadUserClubs() {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id) {
      console.error('No user logged in');
      const clubsMenuItem = this.navItems.find(item => item.name === 'Clubs');
      if (clubsMenuItem && clubsMenuItem.subItems) {
        clubsMenuItem.subItems = [
          { name: "Please login", path: "/user-panel/clubs" }
        ];
      }
      this.cdr.detectChanges();
      return;
    }

    const userId = currentUser.id;
    console.log('Loading clubs for user ID:', userId);
    
    // Get all memberships for the user
    this.memberService.getMembersByUser(userId).subscribe({
      next: (members) => {
        console.log('📋 Members data loaded:', members);
        
        if (members.length === 0) {
          const clubsMenuItem = this.navItems.find(item => item.name === 'Clubs');
          if (clubsMenuItem && clubsMenuItem.subItems) {
            clubsMenuItem.subItems = [
              { name: "No clubs joined yet", path: "/user-panel/clubs" }
            ];
          }
          this.cdr.detectChanges();
          return;
        }
        
        // Store roles
        members.forEach(member => {
          this.clubRoles[member.clubId] = member.rank;
          console.log(`Club ${member.clubId}: Role = ${member.rank}`);
        });
        
        console.log('📊 Club roles map:', this.clubRoles);
        
        // Get club IDs
        const clubIds = members.map(m => m.clubId);
        
        // Load club details for each membership
        const clubRequests = clubIds.map(clubId => this.clubService.getClubById(clubId));
        
        // Use forkJoin to load all clubs in parallel
        if (clubRequests.length > 0) {
          forkJoin(clubRequests).subscribe({
            next: (clubs) => {
              console.log('📚 Clubs loaded:', clubs);
              this.userClubs = clubs;
              
              // Update clubs menu with role information
              const clubsMenuItem = this.navItems.find(item => item.name === 'Clubs');
              if (clubsMenuItem) {
                // Create a completely new array to trigger change detection
                const newSubItems = clubs.map(club => {
                  const isPresident = this.clubRoles[club.id!] === 'PRESIDENT';
                  console.log(`Club "${club.name}" (ID: ${club.id}): isPresident = ${isPresident}, role = ${this.clubRoles[club.id!]}`);
                  return {
                    name: club.name,
                    path: `/user-panel/clubs/${club.id}`,
                    isPresident: isPresident
                  };
                });
                clubsMenuItem.subItems = newSubItems;
                console.log('✅ Updated subItems:', newSubItems);
                console.log('✅ Total clubs in submenu:', newSubItems.length);
              }
              // Force change detection
              this.cdr.markForCheck();
              this.cdr.detectChanges();
            },
            error: (error) => {
              console.error('Error loading club details:', error);
              const clubsMenuItem = this.navItems.find(item => item.name === 'Clubs');
              if (clubsMenuItem && clubsMenuItem.subItems) {
                clubsMenuItem.subItems = [
                  { name: "Error loading clubs", path: "/user-panel/clubs" }
                ];
              }
              this.cdr.detectChanges();
            }
          });
        }
      },
      error: (error) => {
        console.error('Error loading user memberships:', error);
        const clubsMenuItem = this.navItems.find(item => item.name === 'Clubs');
        if (clubsMenuItem && clubsMenuItem.subItems) {
          clubsMenuItem.subItems = [
            { name: "Error loading clubs", path: "/user-panel/clubs" }
          ];
        }
        this.cdr.detectChanges();
      }
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  isActive(path: string): boolean {
    return this.router.url === path;
  }

  toggleSubmenu(section: string, index: number) {
    const key = `${section}-${index}`;

    if (this.openSubmenu === key) {
      this.openSubmenu = null;
      this.subMenuHeights[key] = 0;
    } else {
      this.openSubmenu = key;

      setTimeout(() => {
        const el = document.getElementById(key);
        if (el) {
          this.subMenuHeights[key] = el.scrollHeight;
          this.cdr.detectChanges();
        }
      });
    }
  }

  onSidebarMouseEnter() {
    this.isExpanded$.subscribe(expanded => {
      if (!expanded) {
        this.sidebarService.setHovered(true);
      }
    }).unsubscribe();
  }

  private setActiveMenuFromRoute(currentUrl: string) {
    const menuGroups = [
      { items: this.navItems, prefix: 'main' },
      { items: this.othersItems, prefix: 'others' },
    ];

    menuGroups.forEach(group => {
      group.items.forEach((nav, i) => {
        if (nav.subItems) {
          nav.subItems.forEach(subItem => {
            if (currentUrl === subItem.path) {
              const key = `${group.prefix}-${i}`;
              this.openSubmenu = key;

              setTimeout(() => {
                const el = document.getElementById(key);
                if (el) {
                  this.subMenuHeights[key] = el.scrollHeight;
                  this.cdr.detectChanges();
                }
              });
            }
          });
        }
      });
    });
  }

  onSubmenuClick() {
    this.isMobileOpen$.subscribe(isMobile => {
      if (isMobile) {
        this.sidebarService.setMobileOpen(false);
      }
    }).unsubscribe();
  }
}
