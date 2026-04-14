import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { EventService, Event } from '../../../core/services/event.service';
import { NotificationService } from '../../../core/services/notification.service';
import { EventWebSocketService } from '../../../services/event-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';

@Component({
  selector: 'app-events-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './events-requests.component.html',
  styleUrls: ['./events-requests.component.scss']
})
export class EventsRequestsComponent implements OnInit, OnDestroy {
  allEvents: Event[] = [];
  pendingEvents: Event[] = [];
  approvedEvents: Event[] = [];
  rejectedEvents: Event[] = [];
  
  selectedTab: 'pending' | 'approved' | 'rejected' = 'pending';
  loading = false;
  error: string | null = null;

  // Search / Filter / Sort
  searchQuery = '';
  filterType = '';
  sortBy: 'date_asc' | 'date_desc' | 'title_asc' | 'title_desc' = 'date_desc';
  
  private wsSubscriptions = new Subscription();

  eventTypeIcons: { [key: string]: string } = {
    'WORKSHOP': '🛠️',
    'SEMINAR': '📚',
    'SOCIAL': '🎉'
  };

  constructor(
    private eventService: EventService,
    private notificationService: NotificationService,
    private eventWsService: EventWebSocketService,
    private dataSyncService: DataSyncService
  ) {}

  ngOnInit() {
    this.initializeWebSocket();
    this.setupAutoSync();
    this.loadEvents();
  }
  
  ngOnDestroy() {
    this.wsSubscriptions.unsubscribe();
    this.eventWsService.disconnect();
  }
  
  private async initializeWebSocket() {
    try {
      await this.eventWsService.connect();
      this.eventWsService.subscribeToGlobalEvents();
      console.log('✅ Event WebSocket initialized for events-requests');
    } catch (error) {
      console.error('❌ Failed to initialize WebSocket:', error);
    }
  }
  
  private setupAutoSync() {
    const syncSub = this.dataSyncService.onEventDataChanged().subscribe(change => {
      if (change.action !== 'none') {
        console.log('🔄 Event data changed in events-requests:', change.action);
        this.loadEvents();
      }
    });
    this.wsSubscriptions.add(syncSub);
  }

  loadEvents() {
    this.loading = true;
    this.error = null;

    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        this.allEvents = events;
        this.categorizeEvents();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading events:', err);
        this.error = 'Failed to load events. Please try again.';
        this.loading = false;
      }
    });
  }

  categorizeEvents() {
    this.pendingEvents = this.filterAndSort(this.allEvents.filter(e => e.status === 'PENDING'));
    this.approvedEvents = this.filterAndSort(this.allEvents.filter(e => e.status === 'APPROVED'));
    this.rejectedEvents = this.filterAndSort(this.allEvents.filter(e => e.status === 'REJECTED'));
  }

  filterAndSort(events: Event[]): Event[] {
    let result = [...events];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(e =>
        e.title?.toLowerCase().includes(q) ||
        e.location?.toLowerCase().includes(q)
      );
    }

    if (this.filterType) {
      result = result.filter(e => e.type === this.filterType);
    }

    result.sort((a, b) => {
      switch (this.sortBy) {
        case 'date_asc':  return new Date(a.startDate || '').getTime() - new Date(b.startDate || '').getTime();
        case 'date_desc': return new Date(b.startDate || '').getTime() - new Date(a.startDate || '').getTime();
        case 'title_asc': return (a.title || '').localeCompare(b.title || '');
        case 'title_desc': return (b.title || '').localeCompare(a.title || '');
        default: return 0;
      }
    });

    return result;
  }

  applyFilters() {
    this.categorizeEvents();
  }

  resetFilters() {
    this.searchQuery = '';
    this.filterType = '';
    this.sortBy = 'date_desc';
    this.categorizeEvents();
  }

  approveEvent(eventId: number) {
    if (confirm('Are you sure you want to approve this event?')) {
      this.eventService.approveEvent(eventId).subscribe({
        next: () => {
          this.notificationService.success('Event Approved', 'Event has been approved successfully!');
          this.eventService.notifyEventParticipationChanged();
          this.loadEvents();
        },
        error: (err) => {
          this.notificationService.error('Approval Failed', 'Failed to approve event. Please try again.');
        }
      });
    }
  }

  rejectEvent(eventId: number) {
    if (confirm('Are you sure you want to reject this event?')) {
      this.eventService.rejectEvent(eventId).subscribe({
        next: () => {
          this.notificationService.success('Event Rejected', 'Event has been rejected successfully!');
          this.eventService.notifyEventParticipationChanged();
          this.loadEvents();
        },
        error: (err) => {
          this.notificationService.error('Rejection Failed', 'Failed to reject event. Please try again.');
        }
      });
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getEventIcon(type: string): string {
    return this.eventTypeIcons[type] || '📅';
  }

  /**
   * Check if an event is a modification request
   * An event is considered a modification if it was updated after creation
   */
  isModificationRequest(event: Event): boolean {
    if (!event.createdAt || !event.updatedAt) {
      return false;
    }
    
    const createdTime = new Date(event.createdAt).getTime();
    const updatedTime = new Date(event.updatedAt).getTime();
    
    // If updated more than 5 seconds after creation, it's a modification
    const timeDifference = updatedTime - createdTime;
    return timeDifference > 5000; // 5 seconds threshold
  }
}
