import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ClubWebSocketService, ClubNotification, MemberActivity } from '../../services/club-websocket.service';

@Component({
  selector: 'app-club-notifications',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notifications-container">
      <!-- Connection Status -->
      <div class="connection-status" [class.connected]="isConnected" [class.disconnected]="!isConnected">
        <span class="status-indicator"></span>
        {{ isConnected ? 'Connected' : 'Disconnected' }}
      </div>

      <!-- Club Notifications -->
      <div class="notifications-list" *ngIf="notifications.length > 0">
        <h3>Club Notifications</h3>
        <div class="notification-item" *ngFor="let notification of notifications" 
             [class]="'notification-' + notification.type.toLowerCase()">
          <div class="notification-header">
            <span class="notification-type">{{ notification.type }}</span>
            <span class="notification-time">{{ formatTime(notification.timestamp) }}</span>
          </div>
          <div class="notification-body">
            <strong>{{ notification.clubName }}</strong>
            <p>{{ notification.message }}</p>
          </div>
        </div>
      </div>

      <!-- Member Activities -->
      <div class="activities-list" *ngIf="activities.length > 0">
        <h3>Member Activities</h3>
        <div class="activity-item" *ngFor="let activity of activities"
             [class]="'activity-' + activity.activityType.toLowerCase()">
          <div class="activity-icon">
            <i [class]="getActivityIcon(activity.activityType)"></i>
          </div>
          <div class="activity-content">
            <strong>{{ activity.userName }}</strong>
            <span>{{ getActivityMessage(activity) }}</span>
            <small>{{ formatTime(activity.timestamp) }}</small>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div class="empty-state" *ngIf="notifications.length === 0 && activities.length === 0">
        <p>No notifications yet</p>
      </div>
    </div>
  `,
  styles: [`
    .notifications-container {
      padding: 1rem;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .connection-status {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem;
      border-radius: 4px;
      margin-bottom: 1rem;
      font-size: 0.875rem;
    }

    .connection-status.connected {
      background: #d4edda;
      color: #155724;
    }

    .connection-status.disconnected {
      background: #f8d7da;
      color: #721c24;
    }

    .status-indicator {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: currentColor;
    }

    .notifications-list, .activities-list {
      margin-bottom: 1.5rem;
    }

    .notifications-list h3, .activities-list h3 {
      font-size: 1rem;
      margin-bottom: 0.75rem;
      color: #333;
    }

    .notification-item, .activity-item {
      padding: 0.75rem;
      border-left: 3px solid #007bff;
      background: #f8f9fa;
      margin-bottom: 0.5rem;
      border-radius: 4px;
    }

    .notification-item.notification-member_joined,
    .activity-item.activity-joined {
      border-left-color: #28a745;
    }

    .notification-item.notification-member_left,
    .activity-item.activity-left {
      border-left-color: #dc3545;
    }

    .notification-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 0.5rem;
    }

    .notification-type {
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      color: #6c757d;
    }

    .notification-time {
      font-size: 0.75rem;
      color: #6c757d;
    }

    .notification-body strong {
      color: #007bff;
    }

    .notification-body p {
      margin: 0.25rem 0 0 0;
      color: #495057;
    }

    .activity-item {
      display: flex;
      gap: 0.75rem;
      align-items: start;
    }

    .activity-icon {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #007bff;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .activity-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .activity-content strong {
      color: #333;
    }

    .activity-content span {
      color: #6c757d;
      font-size: 0.875rem;
    }

    .activity-content small {
      color: #adb5bd;
      font-size: 0.75rem;
    }

    .empty-state {
      text-align: center;
      padding: 2rem;
      color: #6c757d;
    }
  `]
})
export class ClubNotificationsComponent implements OnInit, OnDestroy {
  @Input() clubId?: number;
  @Input() showGlobal: boolean = false;
  @Input() maxNotifications: number = 10;

  notifications: ClubNotification[] = [];
  activities: MemberActivity[] = [];
  isConnected: boolean = false;

  private subscriptions: Subscription[] = [];

  constructor(private clubWsService: ClubWebSocketService) {}

  async ngOnInit() {
    try {
      // Connect to WebSocket
      await this.clubWsService.connect();

      // Subscribe to connection status
      this.subscriptions.push(
        this.clubWsService.getConnectionStatus().subscribe(status => {
          this.isConnected = status;
        })
      );

      // Subscribe to specific club or global
      if (this.clubId) {
        this.clubWsService.subscribeToClub(this.clubId);
        
        // Listen to club notifications
        this.subscriptions.push(
          this.clubWsService.getClubNotifications().subscribe(notification => {
            if (notification) {
              this.addNotification(notification);
            }
          })
        );

        // Listen to member activities
        this.subscriptions.push(
          this.clubWsService.getMemberActivities().subscribe(activity => {
            if (activity) {
              this.addActivity(activity);
            }
          })
        );
      }

      if (this.showGlobal) {
        this.clubWsService.subscribeToGlobalClubs();
        
        this.subscriptions.push(
          this.clubWsService.getGlobalNotifications().subscribe(notification => {
            if (notification) {
              this.addNotification(notification);
            }
          })
        );
      }
    } catch (error) {
      console.error('Failed to initialize club notifications:', error);
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.clubWsService.disconnect();
  }

  private addNotification(notification: ClubNotification) {
    this.notifications.unshift(notification);
    if (this.notifications.length > this.maxNotifications) {
      this.notifications.pop();
    }
  }

  private addActivity(activity: MemberActivity) {
    this.activities.unshift(activity);
    if (this.activities.length > this.maxNotifications) {
      this.activities.pop();
    }
  }

  formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    
    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    
    return date.toLocaleDateString();
  }

  getActivityIcon(activityType: string): string {
    switch (activityType) {
      case 'JOINED': return 'bi bi-person-plus-fill';
      case 'LEFT': return 'bi bi-person-dash-fill';
      case 'PROMOTED': return 'bi bi-arrow-up-circle-fill';
      case 'DEMOTED': return 'bi bi-arrow-down-circle-fill';
      default: return 'bi bi-bell-fill';
    }
  }

  getActivityMessage(activity: MemberActivity): string {
    switch (activity.activityType) {
      case 'JOINED': return 'joined the club';
      case 'LEFT': return 'left the club';
      case 'PROMOTED': return `was promoted to ${activity.role}`;
      case 'DEMOTED': return `role changed to ${activity.role}`;
      default: return 'activity';
    }
  }
}
