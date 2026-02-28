import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService, Event } from '../../../core/services/event.service';

@Component({
  selector: 'app-events-manage',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './events-manage.component.html'
})
export class EventsManageComponent implements OnInit {
  allEvents: Event[] = [];
  loading = false;
  error: string | null = null;
  
  // Modal state
  showDetailsModal = false;
  selectedEvent: Event | null = null;

  eventTypeIcons: { [key: string]: string } = {
    'WORKSHOP': '🛠️',
    'SEMINAR': '📚',
    'SOCIAL': '🎉'
  };

  constructor(private eventService: EventService) {}

  ngOnInit() {
    this.loadEvents();
  }

  loadEvents() {
    this.loading = true;
    this.error = null;

    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        // Filtrer uniquement les événements créés (APPROVED et REJECTED)
        this.allEvents = events.filter(e => e.status === 'APPROVED' || e.status === 'REJECTED');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading events:', err);
        this.error = 'Failed to load events. Please try again.';
        this.loading = false;
      }
    });
  }

  viewEventDetails(event: Event) {
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
          alert('Event deleted successfully!');
          this.eventService.notifyEventParticipationChanged();
          this.loadEvents();
        },
        error: (err) => {
          console.error('Error deleting event:', err);
          alert('Failed to delete event. Please try again.');
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

