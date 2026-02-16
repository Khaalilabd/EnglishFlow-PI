import { CommonModule } from '@angular/common';
import { Component, ChangeDetectorRef } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { SafeHtmlPipe } from '../../pipe/safe-html.pipe';
import { combineLatest, Subscription } from 'rxjs';

type NavItem = {
  name: string;
  icon: string;
  path?: string;
  badge?: string;
  badgeColor?: string;
  subItems?: { name: string; path: string; badge?: string }[];
};

@Component({
  standalone: true,
  selector: 'app-student-sidebar',
  imports: [CommonModule, RouterModule, SafeHtmlPipe],
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
      name: "My Clubs",
      path: "/user-panel/clubs",
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

  constructor(
    public sidebarService: SidebarService,
    private router: Router,
    private cdr: ChangeDetectorRef
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

    this.setActiveMenuFromRoute(this.router.url);
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
