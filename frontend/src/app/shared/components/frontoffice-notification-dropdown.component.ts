import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-frontoffice-notification-dropdown',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-dropdown">
      <button
        (click)="toggleDropdown()"
        class="notification-btn"
      >
        <svg class="notification-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
        </svg>
        <span *ngIf="unreadCount > 0" class="notification-badge">
          {{ unreadCount }}
        </span>
      </button>

      <div
        *ngIf="isOpen"
        class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg z-50 max-h-96 overflow-y-auto"
      >
        <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-sm font-semibold text-gray-900">Notifications</h3>
          <button class="text-xs text-primary-600 hover:text-primary-700">Mark all as read</button>
        </div>
        
        <div *ngIf="notifications.length === 0" class="px-4 py-8 text-center text-gray-500">
          <svg class="w-12 h-12 mx-auto mb-2 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
          </svg>
          <p class="text-sm">No notifications yet</p>
        </div>

        <div *ngFor="let notification of notifications" 
             class="px-4 py-3 border-b border-gray-50 hover:bg-gray-50 cursor-pointer"
             [class.bg-blue-50]="!notification.read">
          <div class="flex items-start gap-3">
            <div class="flex-shrink-0 w-2 h-2 mt-2 rounded-full" 
                 [class.bg-blue-500]="!notification.read"
                 [class.bg-gray-300]="notification.read"></div>
            <div class="flex-1">
              <p class="text-sm font-medium text-gray-900">{{ notification.title }}</p>
              <p class="text-xs text-gray-600 mt-1">{{ notification.message }}</p>
              <p class="text-xs text-gray-400 mt-1">{{ notification.time }}</p>
            </div>
          </div>
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
      padding: 8px;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .notification-btn:hover {
      background: rgba(255, 255, 255, 0.2);
    }
    
    .notification-icon {
      width: 24px;
      height: 24px;
      color: #fff;
    }
    
    .notification-badge {
      position: absolute;
      top: -4px;
      right: -4px;
      width: 20px;
      height: 20px;
      background: #C84630;
      color: #fff;
      font-size: 11px;
      font-weight: 600;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})
export class FrontofficeNotificationDropdownComponent {
  isOpen = false;
  notifications: any[] = [];
  unreadCount = 0;

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }
}
