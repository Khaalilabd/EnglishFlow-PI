import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-frontoffice-notification-dropdown',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="notification-dropdown">
      <button
        (click)="toggleDropdown()"
        class="notification-btn"
        [class.has-notifications]="unreadCount > 0"
      >
        <svg class="notification-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
        </svg>
        <span *ngIf="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
      </button>

      <div
        *ngIf="isOpen"
        class="notification-panel"
      >
        <div class="notification-header">
          <h3>Notifications</h3>
          <button *ngIf="notifications.length > 0" (click)="markAllAsRead()" class="mark-all-btn">
            Mark all as read
          </button>
        </div>

        <div class="notification-list">
          <div *ngIf="notifications.length === 0" class="no-notifications">
            <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path>
            </svg>
            <p>No notifications yet</p>
          </div>

          <div
            *ngFor="let notification of notifications"
            class="notification-item"
            [class.unread]="!notification.read"
            (click)="markAsRead(notification)"
          >
            <div class="notification-content">
              <p class="notification-title">{{ notification.title }}</p>
              <p class="notification-message">{{ notification.message }}</p>
              <span class="notification-time">{{ getTimeAgo(notification.timestamp) }}</span>
            </div>
          </div>
        </div>

        <div *ngIf="notifications.length > 0" class="notification-footer">
          <a routerLink="/notifications" (click)="closeDropdown()" class="view-all-link">
            View all notifications
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-dropdown {
      position: relative;
    }
    
    .notification-btn {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      background: transparent;
      border: none;
      border-radius: 50%;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .notification-btn:hover {
      background: rgba(246, 189, 96, 0.15);
    }
    
    .notification-icon {
      width: 28px;
      height: 28px;
      color: #fff;
      filter: drop-shadow(0 1px 3px rgba(0, 0, 0, 0.2));
    }
    
    .notification-btn.has-notifications .notification-icon {
      color: #F6BD60;
      animation: ring 2s ease-in-out infinite;
    }
    
    @keyframes ring {
      0%, 100% { transform: rotate(0deg); }
      10%, 30% { transform: rotate(-10deg); }
      20%, 40% { transform: rotate(10deg); }
    }
    
    .notification-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      min-width: 20px;
      height: 20px;
      padding: 0 6px;
      background: #ef4444;
      color: white;
      font-size: 11px;
      font-weight: 600;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
    }
    
    .notification-panel {
      position: absolute;
      right: 0;
      top: calc(100% + 8px);
      width: 380px;
      max-height: 500px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
      z-index: 50;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }
    
    .notification-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px 20px;
      border-bottom: 1px solid #e5e7eb;
    }
    
    .notification-header h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #111827;
    }
    
    .mark-all-btn {
      background: none;
      border: none;
      color: #F6BD60;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      transition: all 0.2s;
    }
    
    .mark-all-btn:hover {
      background: rgba(246, 189, 96, 0.1);
    }
    
    .notification-list {
      flex: 1;
      overflow-y: auto;
      max-height: 400px;
    }
    
    .no-notifications {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px 20px;
      color: #9ca3af;
    }
    
    .empty-icon {
      width: 64px;
      height: 64px;
      margin-bottom: 12px;
      opacity: 0.5;
    }
    
    .no-notifications p {
      margin: 0;
      font-size: 14px;
    }
    
    .notification-item {
      padding: 16px 20px;
      border-bottom: 1px solid #f3f4f6;
      cursor: pointer;
      transition: background 0.2s;
    }
    
    .notification-item:hover {
      background: #f9fafb;
    }
    
    .notification-item.unread {
      background: #fef3e2;
    }
    
    .notification-item.unread:hover {
      background: #fde9c9;
    }
    
    .notification-content {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    
    .notification-title {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      color: #111827;
    }
    
    .notification-message {
      margin: 0;
      font-size: 13px;
      color: #6b7280;
      line-height: 1.4;
    }
    
    .notification-time {
      font-size: 12px;
      color: #9ca3af;
    }
    
    .notification-footer {
      padding: 12px 20px;
      border-top: 1px solid #e5e7eb;
      text-align: center;
    }
    
    .view-all-link {
      color: #F6BD60;
      font-size: 14px;
      font-weight: 500;
      text-decoration: none;
      transition: color 0.2s;
    }
    
    .view-all-link:hover {
      color: #f5b04a;
    }
    
    @media (max-width: 768px) {
      .notification-panel {
        width: 320px;
      }
    }
  `]
})
export class FrontofficeNotificationDropdownComponent implements OnInit {
  isOpen = false;
  notifications: any[] = [];
  unreadCount = 0;

  ngOnInit() {
    // Load notifications from service
    this.loadNotifications();
  }

  loadNotifications() {
    // Mock data - replace with actual service call
    this.notifications = [];
    this.updateUnreadCount();
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  closeDropdown() {
    this.isOpen = false;
  }

  markAsRead(notification: any) {
    if (!notification.read) {
      notification.read = true;
      this.updateUnreadCount();
      // Call service to mark as read (to be implemented)
    }
  }

  markAllAsRead() {
    this.notifications.forEach(n => n.read = true);
    this.updateUnreadCount();
    // Call service to mark all as read (to be implemented)
  }

  updateUnreadCount() {
    this.unreadCount = this.notifications.filter(n => !n.read).length;
  }

  getTimeAgo(timestamp: Date): string {
    const now = new Date();
    const diff = now.getTime() - new Date(timestamp).getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days}d ago`;
    if (hours > 0) return `${hours}h ago`;
    if (minutes > 0) return `${minutes}m ago`;
    return 'Just now';
  }
}
