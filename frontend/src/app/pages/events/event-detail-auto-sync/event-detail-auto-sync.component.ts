import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { EventWebSocketService } from '../../../services/event-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-event-detail-auto-sync',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="event-detail-container" *ngIf="!loading">
      <!-- Connection Status -->
      <div class="connection-status" [class.connected]="isConnected">
        <i class="bi" [class.bi-wifi]="isConnected" [class.bi-wifi-off]="!isConnected"></i>
        <span>{{ isConnected ? 'Live Updates Active' : 'Offline' }}</span>
      </div>

      <!-- Event Header -->
      <div class="event-header" *ngIf="event">
        <h1>{{ event.title }}</h1>
        <p>{{ event.description }}</p>
        
        <!-- Participant Counter (Auto-updated) -->
        <div class="participant-counter">
          <i class="bi bi-people-fill"></i>
          <span>{{ event.currentParticipants }} / {{ event.maxParticipants }}</span>
          <span class="badge" [class.badge-success]="event.currentParticipants < event.maxParticipants"
                              [class.badge-danger]="event.currentParticipants >= event.maxParticipants">
            {{ event.currentParticipants >= event.maxParticipants ? 'Full' : 'Available' }}
          </span>
        </div>

        <!-- Manual Refresh Button -->
        <button class="btn btn-sm btn-outline-primary" (click)="refreshAll()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <!-- Participants List (Auto-updated) -->
      <div class="participants-section" *ngIf="participants">
        <h2>Participants ({{ participants.length }})</h2>
        <div class="participants-list">
          <div class="participant-card" *ngFor="let participant of participants">
            <span>User {{ participant.userId }}</span>
            <small>Joined: {{ participant.registeredAt | date:'short' }}</small>
          </div>
        </div>
      </div>
    </div>

    <div class="loading-spinner" *ngIf="loading">
      <div class="spinner"></div>
      <p>Loading event details...</p>
    </div>
  `,
  styles: [`
    .connection-status {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      background: #f8d7da;
      color: #721c24;
      font-size: 0.875rem;
      margin-bottom: 1rem;
    }

    .connection-status.connected {
      background: #d4edda;
      color: #155724;
    }

    .participant-counter {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 1.25rem;
      margin: 1rem 0;
    }

    .badge {
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.875rem;
    }

    .badge-success {
      background: #d4edda;
      color: #155724;
    }

    .badge-danger {
      background: #f8d7da;
      color: #721c24;
    }

    .participants-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 1rem;
      margin-top: 1rem;
    }

    .participant-card {
      padding: 1rem;
      border: 1px solid #dee2e6;
      border-radius: 8px;
      background: #fff;
    }

    .loading-spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 400px;
    }

    .spinner {
      width: 50px;
      height: 50px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #007bff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `]
})
export class EventDetailAutoSyncComponent implements OnInit, OnDestroy {
  event: any = null;
  participants: any[] = [];
  loading = true;
  error: string | null = null;
  eventId!: number;
  isConnected = false;
  
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    // private eventService: EventService, // TODO: Injecter votre EventService
    private eventWsService: EventWebSocketService,
    private dataSyncService: DataSyncService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.eventId = +params['id'];
        this.initializeComponent();
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.eventWsService.disconnect();
  }

  private async initializeComponent() {
    try {
      // 1. Charger les données initiales
      await this.loadAllData();

      // 2. Initialiser WebSocket
      await this.initializeWebSocket();

      // 3. Configurer l'auto-sync
      this.setupAutoSync();

      this.loading = false;
      this.cdr.markForCheck();
    } catch (error) {
      console.error('Error initializing component:', error);
      this.error = 'Failed to load event details';
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  private loadAllData(): Promise<void> {
    return new Promise((resolve) => {
      console.log('📥 Loading event data...');
      
      // TODO: Remplacer par vos vrais appels API
      // forkJoin({
      //   event: this.eventService.getEventById(this.eventId),
      //   participants: this.eventService.getEventParticipants(this.eventId)
      // }).subscribe(...)
      
      // Simulation pour l'instant
      setTimeout(() => {
        this.event = {
          id: this.eventId,
          title: 'English Workshop',
          description: 'Practice your English skills',
          currentParticipants: 15,
          maxParticipants: 30
        };
        this.participants = [];
        resolve();
      }, 500);
    });
  }

  private async initializeWebSocket() {
    try {
      console.log('🔌 Connecting to WebSocket...');
      
      await this.eventWsService.connect();

      this.eventWsService.getConnectionStatus()
        .pipe(takeUntil(this.destroy$))
        .subscribe(status => {
          this.isConnected = status;
          console.log(status ? '✅ WebSocket connected' : '❌ WebSocket disconnected');
          this.cdr.markForCheck();
        });

      this.eventWsService.subscribeToEvent(this.eventId);

      console.log('✅ WebSocket initialized successfully');
    } catch (error) {
      console.error('❌ Failed to initialize WebSocket:', error);
    }
  }

  private setupAutoSync() {
    console.log('🔄 Setting up auto-sync...');

    // Auto-refresh quand les données de l'événement changent
    this.dataSyncService.onEventDataChanged()
      .pipe(takeUntil(this.destroy$))
      .subscribe(change => {
        if (change.eventId === this.eventId || !change.eventId) {
          console.log('🔄 Auto-refreshing event data due to:', change.action);
          
          switch (change.action) {
            case 'EVENT_UPDATED':
              this.refreshEventData();
              break;
            
            case 'EVENT_CANCELLED':
              this.handleEventCancelled();
              break;
          }
        }
      });

    // Auto-refresh quand les participants changent
    this.dataSyncService.onParticipantDataChanged()
      .pipe(takeUntil(this.destroy$))
      .subscribe(change => {
        if (change && change.eventId === this.eventId) {
          console.log('🔄 Auto-refreshing participants due to:', change.action);
          this.refreshParticipants();
          this.refreshEventData(); // Pour mettre à jour le compteur
        }
      });
  }

  private refreshEventData() {
    console.log('🔄 Refreshing event data...');
    // TODO: Implémenter avec votre EventService
    // this.eventService.getEventById(this.eventId).subscribe(...)
  }

  private refreshParticipants() {
    console.log('🔄 Refreshing participants list...');
    // TODO: Implémenter avec votre EventService
    // this.eventService.getEventParticipants(this.eventId).subscribe(...)
  }

  private handleEventCancelled() {
    Swal.fire({
      title: 'Event Cancelled',
      text: 'This event has been cancelled',
      icon: 'warning',
      confirmButtonText: 'Go to Events List'
    }).then(() => {
      this.router.navigate(['/events']);
    });
  }

  refreshAll() {
    console.log('🔄 Manual refresh triggered');
    this.loading = true;
    
    this.loadAllData().then(() => {
      this.loading = false;
      Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Data refreshed',
        showConfirmButton: false,
        timer: 2000
      });
    });
  }
}
