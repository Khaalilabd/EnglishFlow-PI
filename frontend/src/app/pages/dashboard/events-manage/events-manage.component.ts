<<<<<<< HEAD
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService, Event } from '../../../core/services/event.service';
import { NotificationService } from '../../../core/services/notification.service';
=======
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { EventService, Event } from '../../../core/services/event.service';
import { NotificationService } from '../../../core/services/notification.service';
import { UserService } from '../../../core/services/user.service';
import { EventWebSocketService } from '../../../services/event-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';

interface EventWithCreator extends Event {
  creatorName?: string;
  creatorEmail?: string;
}
>>>>>>> origin/club/event-service

@Component({
  selector: 'app-events-manage',
  standalone: true,
  imports: [CommonModule],
<<<<<<< HEAD
  templateUrl: './events-manage.component.html'
})
export class EventsManageComponent implements OnInit {
  allEvents: Event[] = [];
=======
  templateUrl: './events-manage.component.html',
  styleUrls: ['./events-manage.component.scss']
})
export class EventsManageComponent implements OnInit, OnDestroy {
  allEvents: EventWithCreator[] = [];
>>>>>>> origin/club/event-service
  loading = false;
  error: string | null = null;
  
  // Modal state
  showDetailsModal = false;
<<<<<<< HEAD
  selectedEvent: Event | null = null;
=======
  selectedEvent: EventWithCreator | null = null;
  
  private wsSubscriptions = new Subscription();
>>>>>>> origin/club/event-service

  eventTypeIcons: { [key: string]: string } = {
    'WORKSHOP': '🛠️',
    'SEMINAR': '📚',
    'SOCIAL': '🎉'
  };

  constructor(
    private eventService: EventService,
<<<<<<< HEAD
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadEvents();
  }
=======
    private notificationService: NotificationService,
    private userService: UserService,
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
      console.log('✅ Event WebSocket initialized for events-manage');
    } catch (error) {
      console.error('❌ Failed to initialize WebSocket:', error);
    }
  }
  
  private setupAutoSync() {
    const syncSub = this.dataSyncService.onEventDataChanged().subscribe(change => {
      if (change.action !== 'none') {
        console.log('🔄 Event data changed in events-manage:', change.action);
        this.loadEvents();
      }
    });
    this.wsSubscriptions.add(syncSub);
  }
>>>>>>> origin/club/event-service

  loadEvents() {
    this.loading = true;
    this.error = null;

    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        // Filtrer uniquement les événements créés (APPROVED et REJECTED)
<<<<<<< HEAD
        this.allEvents = events.filter(e => e.status === 'APPROVED' || e.status === 'REJECTED');
        this.loading = false;
=======
        const filteredEvents = events.filter(e => e.status === 'APPROVED' || e.status === 'REJECTED');
        
        // Enrichir avec les informations du créateur
        this.enrichEventsWithCreatorInfo(filteredEvents);
>>>>>>> origin/club/event-service
      },
      error: (err) => {
        console.error('Error loading events:', err);
        this.error = 'Failed to load events. Please try again.';
        this.loading = false;
      }
    });
  }

<<<<<<< HEAD
  viewEventDetails(event: Event) {
=======
  enrichEventsWithCreatorInfo(events: Event[]) {
    const enrichedEvents: EventWithCreator[] = [];
    let completed = 0;

    if (events.length === 0) {
      this.allEvents = [];
      this.loading = false;
      return;
    }

    events.forEach(event => {
      if (event.creatorId) {
        this.userService.getUserById(event.creatorId).subscribe({
          next: (user) => {
            enrichedEvents.push({
              ...event,
              creatorName: `${user.firstName} ${user.lastName}`,
              creatorEmail: user.email
            });
            completed++;
            if (completed === events.length) {
              this.allEvents = enrichedEvents;
              this.loading = false;
            }
          },
          error: (err) => {
            console.warn(`Could not fetch user ${event.creatorId}:`, err);
            enrichedEvents.push({
              ...event,
              creatorName: 'Unknown User'
            });
            completed++;
            if (completed === events.length) {
              this.allEvents = enrichedEvents;
              this.loading = false;
            }
          }
        });
      } else {
        enrichedEvents.push({
          ...event,
          creatorName: 'Unknown User'
        });
        completed++;
        if (completed === events.length) {
          this.allEvents = enrichedEvents;
          this.loading = false;
        }
      }
    });
  }

  viewEventDetails(event: EventWithCreator) {
>>>>>>> origin/club/event-service
    this.selectedEvent = event;
    this.showDetailsModal = true;
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedEvent = null;
  }

  deleteEvent(eventId: number) {
    if (confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
      this.eventService.deleteEvent(eventId).subscribe({
        next: () => {
          this.notificationService.success('Event Deleted', 'Event has been deleted successfully!');
          this.eventService.notifyEventParticipationChanged();
          this.loadEvents();
        },
        error: (err) => {
          this.notificationService.error('Delete Failed', 'Failed to delete event. Please try again.');
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
}

