import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DropdownComponent } from '../../ui/dropdown/dropdown.component';
import { DropdownItemComponent } from '../../ui/dropdown/dropdown-item/dropdown-item.component';
import { NotificationSseService, NotificationEvent } from '../../../../core/services/notification-sse.service';
import { ComplaintService } from '../../../../core/services/complaint.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-notification-dropdown',
  templateUrl: './notification-dropdown.component.html',
  imports:[CommonModule,RouterModule,DropdownComponent,DropdownItemComponent]
})
export class NotificationDropdownComponent implements OnInit, OnDestroy {
  isOpen = false;
  notifying = false;
  notifications: NotificationEvent[] = [];
  unreadCount = 0;
  private sseSubscription?: Subscription;

  constructor(
    private notificationSseService: NotificationSseService,
    private complaintService: ComplaintService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    console.log('🔍 [NotificationDropdown] ngOnInit called');
    
    // Debug: Log current user
    const currentUser = this.authService.currentUserValue;
    console.log('🔍 [NotificationDropdown] Current user:', currentUser);
    console.log('🔍 [NotificationDropdown] User ID:', currentUser?.id, 'Type:', typeof currentUser?.id);
    console.log('🔍 [NotificationDropdown] User Role:', currentUser?.role);
    
    // Load existing notifications
    console.log('🔍 [NotificationDropdown] Loading notifications...');
    this.loadNotifications();
    
    // Connect to SSE for real-time notifications
    console.log('🔍 [NotificationDropdown] Connecting to SSE...');
    this.connectToSSE();
  }

  ngOnDestroy() {
    if (this.sseSubscription) {
      this.sseSubscription.unsubscribe();
    }
    this.notificationSseService.disconnect();
  }

  private loadNotifications() {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id) return;

    this.complaintService.getUserNotifications(currentUser.id).subscribe({
      next: (notifications: any[]) => {
        this.notifications = notifications; // Charger toutes les notifications
        this.unreadCount = notifications.filter(n => !n.isRead).length;
        this.notifying = this.unreadCount > 0;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
      }
    });
  }

  private connectToSSE() {
    this.sseSubscription = this.notificationSseService.connect().subscribe({
      next: (notification: NotificationEvent) => {
        console.log('New notification received in component:', notification);
        
        // Add to notifications list (sans limite)
        this.notifications.unshift(notification);
        
        // Update unread count
        this.unreadCount++;
        this.notifying = true;
        
        // Force Angular to detect changes
        this.cdr.detectChanges();
        
        // Show browser notification if supported
        this.showBrowserNotification(notification);
      },
      error: (error) => {
        console.error('SSE error in component:', error);
      }
    });
  }

  private showBrowserNotification(notification: NotificationEvent) {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('New Complaint Notification', {
        body: notification.message,
        icon: '/images/logo/jungle-in-english-removebg-preview.png'
      });
    } else if ('Notification' in window && Notification.permission !== 'denied') {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          new Notification('New Complaint Notification', {
            body: notification.message,
            icon: '/images/logo/jungle-in-english-removebg-preview.png'
          });
        }
      });
    }
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.notifying = false;
    }
  }

  closeDropdown() {
    this.isOpen = false;
  }

  markAsRead(notification: NotificationEvent) {
    if (notification.isRead) return;

    this.complaintService.markNotificationAsRead(notification.id).subscribe({
      next: () => {
        notification.isRead = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      },
      error: (error) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  navigateToComplaint(notification: NotificationEvent) {
    // Marquer comme lue
    this.markAsRead(notification);
    // Fermer le dropdown
    this.closeDropdown();
    
    // Rediriger vers le détail de la plainte selon le rôle
    const currentUser = this.authService.currentUserValue;
    let route = '';
    
    if (currentUser?.role === 'ACADEMIC_OFFICE_AFFAIR' || currentUser?.role === 'ADMIN') {
      route = `/dashboard/complaints/${notification.complaintId}`;
    } else if (currentUser?.role === 'TUTOR') {
      route = `/tutor-panel/complaints/${notification.complaintId}`;
    } else if (currentUser?.role === 'STUDENT') {
      route = `/user-panel/complaints/edit/${notification.complaintId}`;
    }
    
    if (route) {
      window.location.href = route;
    }
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'NEW_COMPLAINT':
        return 'fa-exclamation-circle';
      case 'NEW_MESSAGE':
        return 'fa-comment';
      case 'STATUS_CHANGE':
        return 'fa-sync';
      case 'ESCALATION':
        return 'fa-arrow-up';
      default:
        return 'fa-bell';
    }
  }

  getNotificationColor(type: string): string {
    switch (type) {
      case 'NEW_COMPLAINT':
        return 'text-amber-500';
      case 'NEW_MESSAGE':
        return 'text-blue-500';
      case 'STATUS_CHANGE':
        return 'text-green-500';
      case 'ESCALATION':
        return 'text-red-500';
      default:
        return 'text-gray-500';
    }
  }

  getTimeAgo(dateString: string): string {
    const now = new Date();
    const date = new Date(dateString);
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 1) return 'just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    
    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours}h ago`;
    
    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d ago`;
  }
}