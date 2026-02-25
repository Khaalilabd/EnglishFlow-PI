import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService, Event } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { MemberService } from '../../../core/services/member.service';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit, OnDestroy {
  events: Event[] = [];
  upcomingEvents: Event[] = [];
  myEvents: Event[] = [];
  loading = false;
  currentUserId: number | null = null;
  selectedTab: 'all' | 'upcoming' = 'upcoming';
  isAdmin = false;
  canCreateEvent = false; // Nouvelle propriété pour vérifier les permissions

  // Modal states
  showModal = false;
  showDetailsView = false; // Details view (not modal)
  isEditMode = false;
  selectedEvent: Event | null = null;
  
  // Participants modal
  showParticipantsModal = false;
  eventParticipants: any[] = [];
  loadingParticipants = false;

  // Countdown timer
  countdown: {
    days: number;
    hours: number;
    minutes: number;
    seconds: number;
  } = { days: 0, hours: 0, minutes: 0, seconds: 0 };
  countdownInterval: any;

  // Form data
  eventForm: Event = {
    title: '',
    type: 'WORKSHOP',
    eventDate: '',
    location: '',
    maxParticipants: 10,
    description: ''
  };

  eventTypeIcons: { [key: string]: string } = {
    'WORKSHOP': '🛠️',
    'SEMINAR': '📚',
    'SOCIAL': '🎉'
  };

  eventTypeColors: { [key: string]: string } = {
    'WORKSHOP': 'bg-blue-100',
    'SEMINAR': 'bg-purple-100',
    'SOCIAL': 'bg-pink-100'
  };

  constructor(
    private eventService: EventService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private memberService: MemberService
  ) {}

  ngOnInit() {
    // Get current user immediately
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.id) {
      this.currentUserId = currentUser.id;
      this.isAdmin = currentUser.role === 'ADMIN';
      // Vérifier les permissions de création d'événements
      this.checkEventCreationPermission();
    }
    
    // Reset all data on init
    this.myEvents = [];
    this.events = [];
    this.upcomingEvents = [];
    
    // Subscribe to auth changes to update currentUserId when user changes
    this.authService.currentUser$.subscribe((user: any) => {
      if (user && user.id) {
        const previousUserId = this.currentUserId;
        this.currentUserId = user.id;
        this.isAdmin = user.role === 'ADMIN';
        
        // Vérifier les permissions de création d'événements
        this.checkEventCreationPermission();
        
        // Reload events if user changed
        if (previousUserId !== null && previousUserId !== this.currentUserId) {
          // Reset all data before reloading
          this.myEvents = [];
          this.events = [];
          this.upcomingEvents = [];
          this.selectedEvent = null;
          this.showDetailsView = false;
          this.loadEvents();
        }
      }
    });
    
    // Check if there's an event ID in the route
    this.route.paramMap.subscribe(params => {
      const eventId = params.get('id');
      if (eventId) {
        this.loadAndDisplayEvent(Number(eventId));
      } else {
        this.showDetailsView = false;
        this.selectedEvent = null;
        this.loadEvents();
      }
    });
  }

  loadAndDisplayEvent(eventId: number) {
    this.loading = true;
    
    this.eventService.getEventById(eventId).subscribe({
      next: (event) => {
        this.selectedEvent = event;
        this.showDetailsView = true;
        this.loading = false;
        // Start countdown timer
        this.startCountdown();
        // Load user's events to check if registered
        this.loadUserEventsForRegistrationCheck();
      },
      error: (error) => {
        console.error('Error loading event:', error);
        this.loading = false;
        // Fallback to loading all events
        this.loadEvents();
      }
    });
  }

  loadUserEventsForRegistrationCheck() {
    if (!this.currentUserId) return;

    // Get events created by user (all statuses)
    this.eventService.getEventsByCreator(this.currentUserId).subscribe({
      next: (createdEvents) => {
        // Get events user joined
        this.eventService.getUserEvents(this.currentUserId!).subscribe({
          next: (participants) => {
            const joinedEventIds = participants.map(p => p.eventId);
            this.eventService.getAllEvents().subscribe({
              next: (allEvents) => {
                // Only show APPROVED events that user joined
                const joinedEvents = allEvents.filter(e => 
                  e.id && 
                  joinedEventIds.includes(e.id) && 
                  e.status === 'APPROVED'
                );
                // Combine created and joined events (remove duplicates)
                const myEventsMap = new Map<number, Event>();
                [...createdEvents, ...joinedEvents].forEach(event => {
                  if (event.id) {
                    myEventsMap.set(event.id, event);
                  }
                });
                this.myEvents = Array.from(myEventsMap.values());
                console.log('✅ My events loaded for registration check:', this.myEvents);
              },
              error: (error) => console.error('Error loading all events for registration check:', error)
            });
          },
          error: (error) => console.error('Error loading user events for registration check:', error)
        });
      },
      error: (error) => console.error('Error loading created events for registration check:', error)
    });
  }

  ngOnDestroy() {
    // Clear countdown interval when component is destroyed
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  startCountdown() {
    // Clear any existing interval
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }

    if (!this.selectedEvent) return;

    // Update countdown immediately
    this.updateCountdown();

    // Update countdown every second
    this.countdownInterval = setInterval(() => {
      this.updateCountdown();
    }, 1000);
  }

  updateCountdown() {
    if (!this.selectedEvent) return;

    const now = new Date().getTime();
    const eventDate = new Date(this.selectedEvent.eventDate).getTime();
    const distance = eventDate - now;

    if (distance < 0) {
      // Event has started or passed
      this.countdown = { days: 0, hours: 0, minutes: 0, seconds: 0 };
      if (this.countdownInterval) {
        clearInterval(this.countdownInterval);
      }
      return;
    }

    // Calculate time units
    this.countdown = {
      days: Math.floor(distance / (1000 * 60 * 60 * 24)),
      hours: Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
      minutes: Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)),
      seconds: Math.floor((distance % (1000 * 60)) / 1000)
    };
  }

  isEventStarted(): boolean {
    if (!this.selectedEvent) return false;
    const now = new Date().getTime();
    const eventDate = new Date(this.selectedEvent.eventDate).getTime();
    return now >= eventDate;
  }

  loadEvents() {
    this.loading = true;
    
    // Load all events
    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        // Filter to show only APPROVED events (or events created by current user)
        const filteredEvents = events.filter(event => 
          event.status === 'APPROVED' || event.creatorId === this.currentUserId
        );
        this.events = this.filterAvailableEvents(filteredEvents);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.loading = false;
      }
    });

    // Load upcoming events (within 3 days)
    this.eventService.getUpcomingEvents().subscribe({
      next: (events) => {
        // Filter to show only APPROVED events (or events created by current user)
        const filteredEvents = events.filter(event => 
          event.status === 'APPROVED' || event.creatorId === this.currentUserId
        );
        this.upcomingEvents = this.filterUpcomingEvents(filteredEvents);
      },
      error: (error) => {
        console.error('Error loading upcoming events:', error);
      }
    });

    // Load user's events (created + joined)
    if (this.currentUserId) {
      // Get events created by user (all statuses for creator)
      this.eventService.getEventsByCreator(this.currentUserId).subscribe({
        next: (createdEvents) => {
          // Get events user joined
          this.eventService.getUserEvents(this.currentUserId!).subscribe({
            next: (participants) => {
              const joinedEventIds = participants.map(p => p.eventId);
              this.eventService.getAllEvents().subscribe({
                next: (allEvents) => {
                  // Only show APPROVED events that user joined (not their own pending events)
                  const joinedEvents = allEvents.filter(e => 
                    e.id && 
                    joinedEventIds.includes(e.id) && 
                    e.status === 'APPROVED'
                  );
                  // Combine created and joined events (remove duplicates)
                  const myEventsMap = new Map<number, Event>();
                  [...createdEvents, ...joinedEvents].forEach(event => {
                    if (event.id) {
                      myEventsMap.set(event.id, event);
                    }
                  });
                  this.myEvents = Array.from(myEventsMap.values());
                },
                error: (error) => console.error('Error loading all events for my events:', error)
              });
            },
            error: (error) => console.error('Error loading user events:', error)
          });
        },
        error: (error) => console.error('Error loading created events:', error)
      });
    }
  }

  // Filter events that are within 3 days or already started
  filterAvailableEvents(events: Event[]): Event[] {
    const now = new Date();
    const threeDaysFromNow = new Date(now.getTime() + (3 * 24 * 60 * 60 * 1000));
    
    return events.filter(event => {
      const eventDate = new Date(event.eventDate);
      return eventDate <= threeDaysFromNow;
    });
  }

  // Filter events that are more than 3 days away (coming soon)
  filterUpcomingEvents(events: Event[]): Event[] {
    const now = new Date();
    const threeDaysFromNow = new Date(now.getTime() + (3 * 24 * 60 * 60 * 1000));
    
    return events.filter(event => {
      const eventDate = new Date(event.eventDate);
      return eventDate > threeDaysFromNow;
    });
  }

  // Check if event is coming soon (more than 3 days away)
  isComingSoon(event: Event): boolean {
    const now = new Date();
    const threeDaysFromNow = new Date(now.getTime() + (3 * 24 * 60 * 60 * 1000));
    const eventDate = new Date(event.eventDate);
    return eventDate > threeDaysFromNow;
  }

  // Check if user is the creator of the event
  isEventCreator(event: Event): boolean {
    const result = event.creatorId === this.currentUserId;
    console.log('isEventCreator check:', {
      eventId: event.id,
      eventTitle: event.title,
      eventCreatorId: event.creatorId,
      currentUserId: this.currentUserId,
      isCreator: result
    });
    return result;
  }

  openCreateModal() {
    this.isEditMode = false;
    this.eventForm = {
      title: '',
      type: 'WORKSHOP',
      eventDate: '',
      location: '',
      maxParticipants: 10,
      description: ''
    };
    this.showModal = true;
  }

  openEditModal(event: Event) {
    this.isEditMode = true;
    this.eventForm = { ...event };
    // Convert date format for datetime-local input
    if (this.eventForm.eventDate) {
      const date = new Date(this.eventForm.eventDate);
      this.eventForm.eventDate = date.toISOString().slice(0, 16);
    }
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.isEditMode = false;
  }

  saveEvent() {
    // Add creatorId when creating a new event
    if (!this.isEditMode && this.currentUserId) {
      this.eventForm.creatorId = this.currentUserId;
    }

    if (this.isEditMode && this.eventForm.id) {
      const eventId = this.eventForm.id;
      this.eventService.updateEvent(eventId, this.eventForm).subscribe({
        next: (updatedEvent) => {
          alert('Event updated successfully!');
          this.closeModal();
          
          // If we're in details view, update the selected event
          if (this.showDetailsView && this.selectedEvent?.id === eventId) {
            this.selectedEvent = { ...updatedEvent };
          }
          
          // Reload events list
          this.loadEvents();
        },
        error: (error) => {
          console.error('Error updating event:', error);
          const errorMessage = error.error?.message || 'Failed to update event';
          alert(errorMessage);
        }
      });
    } else {
      this.eventService.createEvent(this.eventForm).subscribe({
        next: () => {
          alert('Event created successfully!');
          this.closeModal();
          this.loadEvents();
        },
        error: (error) => {
          console.error('Error creating event:', error);
          const errorMessage = error.error?.message || 'Failed to create event';
          alert(errorMessage);
        }
      });
    }
  }

  deleteEvent(eventId: number) {
    if (confirm('Are you sure you want to delete this event?')) {
      this.eventService.deleteEvent(eventId).subscribe({
        next: () => {
          alert('Event deleted successfully!');
          this.loadEvents();
        },
        error: (error) => {
          console.error('Error deleting event:', error);
          alert('Failed to delete event');
        }
      });
    }
  }

  viewEventDetails(event: Event) {
    this.router.navigate(['/user-panel/events', event.id]);
  }

  closeDetailsView() {
    this.showDetailsView = false;
    this.selectedEvent = null;
    // Clear countdown interval
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
    this.router.navigate(['/user-panel/events']);
  }

  joinEvent(eventId: number) {
    if (!this.currentUserId) {
      alert('Please login to join events');
      return;
    }

    this.eventService.joinEvent(eventId, this.currentUserId).subscribe({
      next: () => {
        alert('Successfully joined the event!');
        // Notify that event participation has changed
        this.eventService.notifyEventParticipationChanged();
        if (this.showDetailsView && this.selectedEvent?.id === eventId) {
          // Reload the event details
          this.loadAndDisplayEvent(eventId);
        } else {
          this.loadEvents();
        }
      },
      error: (error) => {
        console.error('Error joining event:', error);
        alert(error.error?.message || 'Failed to join event');
      }
    });
  }

  leaveEvent(eventId: number) {
    if (!this.currentUserId) return;

    if (confirm('Are you sure you want to leave this event?')) {
      this.eventService.leaveEvent(eventId, this.currentUserId).subscribe({
        next: () => {
          alert('Successfully left the event');
          // Notify that event participation has changed
          this.eventService.notifyEventParticipationChanged();
          if (this.showDetailsView && this.selectedEvent?.id === eventId) {
            // Reload the event details
            this.loadAndDisplayEvent(eventId);
          } else {
            this.loadEvents();
          }
        },
        error: (error) => {
          console.error('Error leaving event:', error);
          alert('Failed to leave event');
        }
      });
    }
  }

  isEventFull(event: Event): boolean {
    return (event.currentParticipants || 0) >= event.maxParticipants;
  }

  isUserRegistered(eventId: number): boolean {
    return this.myEvents.some(e => e.id === eventId);
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

  getEventColor(type: string): string {
    return this.eventTypeColors[type] || 'bg-gray-100';
  }

  selectTab(tab: 'all' | 'upcoming') {
    this.selectedTab = tab;
  }

  getDisplayEvents(): Event[] {
    switch (this.selectedTab) {
      case 'all':
        return this.events;
      case 'upcoming':
        return this.upcomingEvents;
      default:
        return this.events;
    }
  }

  // Image handling methods
  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Check file size (max 2MB)
      if (file.size > 2 * 1024 * 1024) {
        alert('Image size must be less than 2MB');
        return;
      }

      // Check file type
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }

      // Convert to base64
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.eventForm.image = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage() {
    this.eventForm.image = undefined;
  }

  // Participants management
  openParticipantsModal(eventId: number) {
    if (!this.isEventCreator(this.selectedEvent!)) {
      alert('Only the event creator can view participants.');
      return;
    }
    
    this.showParticipantsModal = true;
    this.loadEventParticipants(eventId);
  }

  closeParticipantsModal() {
    this.showParticipantsModal = false;
    this.eventParticipants = [];
  }

  loadEventParticipants(eventId: number) {
    this.loadingParticipants = true;
    this.eventService.getEventParticipants(eventId).subscribe({
      next: (participants) => {
        console.log('📋 Raw participants from API:', participants);
        
        // Extract all unique user IDs
        const userIds = [...new Set(participants.map(p => p.userId))];
        console.log('🔍 Fetching user details for IDs:', userIds);
        
        // Fetch user details for all participants
        this.userService.getUsersByIds(userIds).subscribe({
          next: (users) => {
            console.log('👤 Users fetched from API:', users);
            
            // Create a map of userId -> user details
            const userMap = new Map(users.map(u => [u.id, u]));
            
            // Merge participant data with user details
            this.eventParticipants = participants.map(participant => {
              const user = userMap.get(participant.userId);
              return {
                ...participant,
                firstName: user?.firstName || '',
                lastName: user?.lastName || '',
                image: user?.image || null,
                email: user?.email || ''
              };
            });
            
            this.loadingParticipants = false;
            console.log('📋 Final event participants with user details:', this.eventParticipants);
          },
          error: (err) => {
            console.error('❌ Error loading user details:', err);
            
            // Fallback: show participants without full user details
            this.eventParticipants = participants.map(participant => ({
              ...participant,
              firstName: '',
              lastName: '',
              image: null,
              email: ''
            }));
            this.loadingParticipants = false;
          }
        });
      },
      error: (err) => {
        console.error('❌ Error loading event participants:', err);
        this.loadingParticipants = false;
        alert('Failed to load event participants.');
      }
    });
  }

  removeParticipant(participantId: number, userId: number, eventId: number) {
    if (confirm('Are you sure you want to remove this participant from the event?')) {
      this.eventService.leaveEvent(eventId, userId).subscribe({
        next: () => {
          console.log('✅ Participant removed successfully');
          alert('Participant removed successfully!');
          // Reload participants
          this.loadEventParticipants(eventId);
          // Reload event details to update count
          this.loadAndDisplayEvent(eventId);
        },
        error: (err) => {
          console.error('❌ Error removing participant:', err);
          alert('Failed to remove participant. Please try again.');
        }
      });
    }
  }

  /**
   * Vérifie si l'utilisateur peut créer des événements
   * Seuls les présidents, vice-présidents et event managers peuvent créer des événements
   */
  checkEventCreationPermission() {
    if (!this.currentUserId) {
      this.canCreateEvent = false;
      return;
    }

    const ALLOWED_RANKS = ['PRESIDENT', 'VICE_PRESIDENT', 'EVENT_MANAGER'];

    this.memberService.getMembersByUser(this.currentUserId).subscribe({
      next: (memberships: any[]) => {
        // Vérifier si l'utilisateur a au moins un rôle autorisé dans un club
        this.canCreateEvent = memberships.some((member: any) => 
          ALLOWED_RANKS.includes(member.rank)
        );
        console.log('🔐 Can create event:', this.canCreateEvent, 'User memberships:', memberships);
      },
      error: (err: any) => {
        console.error('❌ Error checking event creation permission:', err);
        this.canCreateEvent = false;
      }
    });
  }
}
