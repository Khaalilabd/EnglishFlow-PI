import { CommonModule } from '@angular/common';
import { Component, ChangeDetectorRef, OnInit, OnDestroy } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';

type SubItem = {
  name: string;
  path: string;
  badge?: string;
  badgeColor?: string;
};

type NavItem = {
  name: string;
  icon: string;
  path?: string;
  badge?: string;
  badgeColor?: string;
  subItems?: SubItem[];
};

type NavSection = {
  id: string;
  title: string;
  icon: string;
  items: NavItem[];
};

@Component({
  standalone: true,
  selector: 'app-sponsor-sidebar',
  imports: [CommonModule, RouterModule],
  templateUrl: './sponsor-sidebar.component.html',
})
export class SponsorSidebarComponent implements OnInit, OnDestroy {

  navSections: NavSection[] = [
    {
      id: 'home',
      title: 'HOME',
      icon: 'fas fa-home',
      items: [
        {
          icon: 'fas fa-th-large',
          name: 'Dashboard',
          path: '/sponsor-panel/dashboard'
        }
      ]
    },
    {
      id: 'sponsorship',
      title: 'SPONSORSHIP',
      icon: 'fas fa-handshake',
      items: [
        {
          icon: 'fas fa-users',
          name: 'Sponsor a Club',
          path: '/sponsor-panel/clubs'
        },
        {
          icon: 'fas fa-chart-line',
          name: 'My Impact',
          path: '/sponsor-panel/my-impact'
        },
        {
          icon: 'fas fa-trophy',
          name: 'Sponsorship Level',
          path: '/sponsor-panel/sponsorship-level'
        }
      ]
    },
    {
      id: 'account',
      title: 'MY ACCOUNT',
      icon: 'fas fa-user-circle',
      items: [
        {
          icon: 'fas fa-building',
          name: 'Company Profile',
          path: '/sponsor-panel/company-profile'
        },
        {
          icon: 'fas fa-cog',
          name: 'Settings',
          path: '/sponsor-panel/settings'
        }
      ]
    },
    {
      id: 'support',
      title: 'SUPPORT',
      icon: 'fas fa-life-ring',
      items: [
        {
          icon: 'fas fa-life-ring',
          name: 'Help Center',
          path: '/sponsor-panel/dashboard'
        }
      ]
    }
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
    private cdr: ChangeDetectorRef,
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

    this.setActiveMenuFromRoute(this.router.url);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  toggleSubmenu(sectionId: string, index: number) {
    const key = `${sectionId}-${index}`;
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

  onSubmenuClick() {
    this.isMobileOpen$.subscribe(isMobile => {
      if (isMobile) {
        this.sidebarService.setMobileOpen(false);
      }
    }).unsubscribe();
  }

  logout() {
    this.authService.logout().subscribe({
      complete: () => this.router.navigate(['/'])
    });
  }

  private setActiveMenuFromRoute(currentUrl: string) {
    this.navSections.forEach(section => {
      section.items.forEach((nav, i) => {
        if (nav.subItems) {
          nav.subItems.forEach(subItem => {
            if (currentUrl === subItem.path) {
              const key = `${section.id}-${i}`;
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
}
