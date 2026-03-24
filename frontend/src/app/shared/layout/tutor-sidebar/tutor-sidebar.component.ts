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
  
  navSections = [
    {
      id: 'home',
      title: 'HOME',
      icon: 'fas fa-home',
      items: [
        { icon: 'fas fa-th-large', label: 'Dashboard', route: '/tutor-panel' }
      ]
    },
    {
      id: 'teaching',
      title: 'TEACHING',
      icon: 'fas fa-chalkboard-teacher',
      items: [
        { icon: 'fas fa-chalkboard', label: 'My Courses', route: '/tutor-panel/courses', badge: 5 },
        { icon: 'fas fa-tablet-alt', label: 'Ebooks', route: '/tutor-panel/ebooks' },
        { icon: 'fas fa-calendar-alt', label: 'Schedule', route: '/tutor-panel/schedule' },
        { icon: 'fas fa-clock', label: 'Availability', route: '/tutor-panel/availability' }
      ]
    },
    {
      id: 'assessment',
      title: 'ASSESSMENT',
      icon: 'fas fa-clipboard-check',
      items: [
        { icon: 'fas fa-tasks', label: 'Assignments', route: '/tutor-panel/assignments' },
        { icon: 'fas fa-clipboard-check', label: 'Quiz Management', route: '/tutor-panel/quiz-management' },
        { icon: 'fas fa-certificate', label: 'Create Exam', route: '/tutor-panel/create-exam' },
        { icon: 'fas fa-award', label: 'Exam Grading', route: '/tutor-panel/exam-grading' }
      ]
    },
    {
      id: 'students',
      title: 'STUDENTS',
      icon: 'fas fa-user-graduate',
      items: [
        { icon: 'fas fa-users', label: 'My Students', route: '/tutor-panel/students', badge: 24 },
        { icon: 'fas fa-chart-line', label: 'Analytics', route: '/tutor-panel/analytics' }
      ]
    },
    {
      id: 'communication',
      title: 'COMMUNICATION',
      icon: 'fas fa-comments',
      items: [
        { icon: 'fas fa-comments', label: 'Messages', route: '/tutor-panel/messages', badge: 3 },
        { icon: 'fas fa-comment-dots', label: 'Forum', route: '/tutor-panel/forum' },
        { icon: 'fas fa-exclamation-triangle', label: 'Complaints', route: '/tutor-panel/complaints' }
      ]
    },
    {
      id: 'account',
      title: 'MY ACCOUNT',
      icon: 'fas fa-user-cog',
      items: [
        { icon: 'fas fa-user', label: 'Profile', route: '/tutor-panel/profile' },
        { icon: 'fas fa-cog', label: 'Settings', route: '/tutor-panel/settings' },
        { icon: 'fas fa-desktop', label: 'Sessions', route: '/dashboard/sessions' },
        { icon: 'fas fa-life-ring', label: 'Help & Support', route: '/tutor-panel/support' }
      ]
    }
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
