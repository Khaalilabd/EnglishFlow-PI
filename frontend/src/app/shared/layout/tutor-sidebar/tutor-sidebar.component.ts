import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SidebarService } from '../../services/sidebar.service';

interface MenuItem {
  icon: string;
  label: string;
  route: string;
  badge?: number;
  section?: 'menu' | 'support';
}

@Component({
  selector: 'app-tutor-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tutor-sidebar.component.html',
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class TutorSidebarComponent implements OnInit {
  isCollapsed = false;
  
  menuItems: MenuItem[] = [
    { icon: 'fas fa-home', label: 'Dashboard', route: '/tutor-panel', section: 'menu' },
    { icon: 'fas fa-globe', label: 'Home', route: '/', section: 'menu' },
    { icon: 'fas fa-book', label: 'My Courses', route: '/tutor-panel/courses', badge: 5, section: 'menu' },
    { icon: 'fas fa-clipboard-list', label: 'Quiz Management', route: '/tutor-panel/quiz-management', section: 'menu' },
    { icon: 'fas fa-book-open', label: 'Ebooks', route: '/tutor-panel/ebooks', section: 'menu' },
    { icon: 'fas fa-calendar-alt', label: 'Schedule', route: '/tutor-panel/schedule', section: 'menu' },
    { icon: 'fas fa-users', label: 'My Students', route: '/tutor-panel/students', badge: 24, section: 'menu' },
    { icon: 'fas fa-tasks', label: 'Assignments', route: '/tutor-panel/assignments', section: 'menu' },
    { icon: 'fas fa-chart-line', label: 'Analytics', route: '/tutor-panel/analytics', section: 'menu' },
    { icon: 'fas fa-comments', label: 'Messages', route: '/tutor-panel/messages', badge: 3, section: 'menu' },
    { icon: 'fas fa-comment-dots', label: 'Forum', route: '/tutor-panel/forum', section: 'menu' },
  ];

  supportItems: MenuItem[] = [
    { icon: 'fas fa-life-ring', label: 'Help & Support', route: '/tutor-panel/support', section: 'support' },
    { icon: 'fas fa-cog', label: 'Settings', route: '/tutor-panel/settings', section: 'support' },
  ];

  constructor(
    private router: Router,
    private sidebarService: SidebarService
  ) {}

  ngOnInit() {
    this.sidebarService.isExpanded$.subscribe((expanded: boolean) => {
      this.isCollapsed = !expanded;
    });
  }

  toggleSidebar() {
    this.sidebarService.toggle();
  }

  isActive(route: string): boolean {
    return this.router.url === route || this.router.url.startsWith(route + '/');
  }
}
