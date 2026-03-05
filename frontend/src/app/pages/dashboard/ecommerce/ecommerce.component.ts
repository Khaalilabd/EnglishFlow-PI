import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface Stat {
  title: string;
  value: string;
  icon: string;
  color: string;
  change: string;
  changeType: 'up' | 'down';
}

interface QuickAction {
  title: string;
  icon: string;
  color: string;
  route: string;
  count?: string;
}

interface RecentActivity {
  id: number;
  userName: string;
  avatar: string;
  action: string;
  time: string;
  type: 'user' | 'course' | 'club' | 'complaint';
}

@Component({
  selector: 'app-ecommerce',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ecommerce.component.html',
  styleUrl: './ecommerce.component.scss'
})
export class EcommerceComponent implements OnInit {
  currentUser: any;
  isAdmin = false;
  isAcademic = false;

  stats: Stat[] = [];
  quickActions: QuickAction[] = [];
  recentActivities: RecentActivity[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.isAdmin = this.currentUser?.role === 'ADMIN';
    this.isAcademic = this.currentUser?.role === 'ACADEMIC_OFFICE_AFFAIR';
    
    this.initializeStats();
    this.initializeQuickActions();
    this.initializeRecentActivities();
  }

  initializeStats() {
    if (this.isAdmin) {
      this.stats = [
        {
          title: 'Total Students',
          value: '1,234',
          icon: '👨‍🎓',
          color: 'from-blue-500 to-blue-600',
          change: '+12% this month',
          changeType: 'up'
        },
        {
          title: 'Active Tutors',
          value: '89',
          icon: '👨‍🏫',
          color: 'from-green-500 to-green-600',
          change: '+5 new tutors',
          changeType: 'up'
        },
        {
          title: 'Total Revenue',
          value: '$45.2K',
          icon: '💰',
          color: 'from-amber-500 to-amber-600',
          change: '+18% this month',
          changeType: 'up'
        },
        {
          title: 'Active Courses',
          value: '156',
          icon: '📚',
          color: 'from-purple-500 to-purple-600',
          change: '+8 new courses',
          changeType: 'up'
        }
      ];
    } else if (this.isAcademic) {
      this.stats = [
        {
          title: 'Total Students',
          value: '1,234',
          icon: '👨‍🎓',
          color: 'from-blue-500 to-blue-600',
          change: '+12% this month',
          changeType: 'up'
        },
        {
          title: 'Active Courses',
          value: '156',
          icon: '📚',
          color: 'from-green-500 to-green-600',
          change: '+8 new courses',
          changeType: 'up'
        },
        {
          title: 'Pending Requests',
          value: '23',
          icon: '📝',
          color: 'from-amber-500 to-amber-600',
          change: 'Needs attention',
          changeType: 'down'
        },
        {
          title: 'Active Clubs',
          value: '45',
          icon: '🎯',
          color: 'from-purple-500 to-purple-600',
          change: '+3 this week',
          changeType: 'up'
        }
      ];
    }
  }

  initializeQuickActions() {
    if (this.isAdmin) {
      this.quickActions = [
        { title: 'Students', icon: '👨‍🎓', color: 'bg-blue-100', route: '/dashboard/students', count: '1,234' },
        { title: 'Tutors', icon: '👨‍🏫', color: 'bg-green-100', route: '/dashboard/tutors', count: '89' },
        { title: 'Statistics', icon: '📊', color: 'bg-purple-100', route: '/dashboard/statistics' },
        { title: 'Gamification', icon: '🎮', color: 'bg-amber-100', route: '/dashboard/gamification' },
        { title: 'Ebooks', icon: '📚', color: 'bg-pink-100', route: '/dashboard/ebooks', count: '342' },
        { title: 'Forum', icon: '💬', color: 'bg-indigo-100', route: '/dashboard/forum' }
      ];
    } else if (this.isAcademic) {
      this.quickActions = [
        { title: 'Students', icon: '👨‍🎓', color: 'bg-blue-100', route: '/dashboard/students', count: '1,234' },
        { title: 'Courses', icon: '📚', color: 'bg-green-100', route: '/dashboard/courses', count: '156' },
        { title: 'Clubs', icon: '🎯', color: 'bg-purple-100', route: '/dashboard/clubs/manage', count: '45' },
        { title: 'Events', icon: '📅', color: 'bg-amber-100', route: '/dashboard/events/manage', count: '28' },
        { title: 'Complaints', icon: '📝', color: 'bg-red-100', route: '/dashboard/complaints', count: '12' },
        { title: 'Forum', icon: '💬', color: 'bg-indigo-100', route: '/dashboard/forum' }
      ];
    }
  }

  initializeRecentActivities() {
    this.recentActivities = [
      { id: 1, userName: 'John Doe', avatar: 'https://i.pravatar.cc/40?img=1', action: 'Enrolled in Business English', time: '5 min ago', type: 'user' },
      { id: 2, userName: 'Sarah Smith', avatar: 'https://i.pravatar.cc/40?img=2', action: 'Completed Grammar Basics', time: '12 min ago', type: 'course' },
      { id: 3, userName: 'Mike Johnson', avatar: 'https://i.pravatar.cc/40?img=3', action: 'Joined Conversation Club', time: '25 min ago', type: 'club' },
      { id: 4, userName: 'Emily Davis', avatar: 'https://i.pravatar.cc/40?img=4', action: 'Submitted a complaint', time: '1 hour ago', type: 'complaint' },
      { id: 5, userName: 'Alex Brown', avatar: 'https://i.pravatar.cc/40?img=5', action: 'Posted in forum', time: '2 hours ago', type: 'user' }
    ];
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'user': return '👤';
      case 'course': return '📚';
      case 'club': return '🎯';
      case 'complaint': return '📝';
      default: return '📌';
    }
  }

  getActivityColor(type: string): string {
    switch (type) {
      case 'user': return 'bg-blue-100';
      case 'course': return 'bg-green-100';
      case 'club': return 'bg-purple-100';
      case 'complaint': return 'bg-red-100';
      default: return 'bg-gray-100';
    }
  }
}
