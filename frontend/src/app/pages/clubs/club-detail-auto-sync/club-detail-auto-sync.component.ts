import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ClubService } from '../../../core/services/club.service';
import { MemberService } from '../../../core/services/member.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { ClubWebSocketService } from '../../../services/club-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';
import { Club, Member } from '../../../core/models/club.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-club-detail-auto-sync',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="club-detail-container" *ngIf="!loading">
      <!-- Connection Status -->
      <div class="connection-status" [class.connected]="isConnected">
        <span class="status-dot"></span>
        <span>{{ isConnected ? 'Live Updates Active' : 'Offline' }}</span>
      </div>

      <!-- Club Header -->
      <div class="club-header" *ngIf="club">
        <h1>{{ club.name }}</h1>
        <p>{{ club.description }}</p>
        
        <!-- Member Counter (Auto-updated) -->
        <div class="member-counter">
          <i class="bi bi-people-fill"></i>
          <span>{{ members.length }} / {{ club.maxMembers }} members</span>
        </div>

        <!-- Manual Refresh Button -->
        <button class="btn btn-sm btn-outline-primary" (click)="refreshAll()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <!-- Members List (Auto-updated) -->
      <div class="members-section" *ngIf="members.length > 0">
        <h2>Members ({{ members.length }})</h2>
        <div class="members-list">
          <div class="member-card" *ngFor="let member of members">
            <div class="member-info">
              <img *ngIf="member.userPhoto" 
                   [src]="member.userPhoto" 
                   alt="{{ member.userName }}"
                   class="member-avatar">
              <div *ngIf="!member.userPhoto" class="member-avatar-placeholder">
                {{ getInitials(member.userName) }}
              </div>
              <div class="member-details">
                <span class="member-name">{{ member.userName || 'User ' + member.userId }}</span>
                <span class="member-email">{{ member.userEmail || '' }}</span>
              </div>
            </div>
            <span class="role-badge">{{ member.rank }}</span>
            <button class="btn btn-sm btn-danger" 
                    *ngIf="member.rank !== 'PRESIDENT'"
                    (click)="removeMember(member.userId)">
              Remove
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="loading-spinner" *ngIf="loading">
      <div class="spinner"></div>
      <p>Loading club details...</p>
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

    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: currentColor;
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.5; }
    }

    .member-counter {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 1.25rem;
      margin: 1rem 0;
    }

    .members-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 1rem;
      margin-top: 1rem;
    }

    .member-card {
      padding: 1rem;
      border: 1px solid #dee2e6;
      border-radius: 8px;
      background: #fff;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
    }

    .member-info {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      flex: 1;
    }

    .member-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
    }

    .member-avatar-placeholder {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: #007bff;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 0.875rem;
    }

    .member-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .member-name {
      font-weight: 600;
      color: #212529;
    }

    .member-email {
      font-size: 0.875rem;
      color: #6c757d;
    }

    .role-badge {
      padding: 0.25rem 0.5rem;
      background: #007bff;
      color: white;
      border-radius: 4px;
      font-size: 0.75rem;
      white-space: nowrap;
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
export class ClubDetailAutoSyncComponent implements OnInit, OnDestroy {
  club: Club | null = null;
  members: Member[] = [];
  loading = true;
  error: string | null = null;
  clubId!: number;
  isConnected = false;
  
  private destroy$ = new Subject<void>();
  private currentUserId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private memberService: MemberService,
    private authService: AuthService,
    private userService: UserService,
    private clubWsService: ClubWebSocketService,
    private dataSyncService: DataSyncService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    // Récupérer l'ID du club
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.clubId = +params['id'];
        this.initializeComponent();
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.clubWsService.disconnect();
  }

  /**
   * Initialisation complète du composant
   */
  private async initializeComponent() {
    try {
      // 1. Charger l'utilisateur actuel
      await this.loadCurrentUser();

      // 2. Charger les données initiales
      await this.loadAllData();

      // 3. Initialiser WebSocket
      await this.initializeWebSocket();

      // 4. Configurer l'auto-sync
      this.setupAutoSync();

      this.loading = false;
      this.cdr.markForCheck();
    } catch (error) {
      console.error('Error initializing component:', error);
      this.error = 'Failed to load club details';
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  /**
   * Charger l'utilisateur actuel
   */
  private loadCurrentUser(): Promise<void> {
    return new Promise((resolve) => {
      this.authService.currentUser$
        .pipe(takeUntil(this.destroy$))
        .subscribe(user => {
          if (user && user.id) {
            this.currentUserId = user.id;
          }
          resolve();
        });
    });
  }

  /**
   * Charger toutes les données en parallèle
   */
  private loadAllData(): Promise<void> {
    return new Promise((resolve, reject) => {
      console.log('📥 Loading club data...');
      
      forkJoin({
        club: this.clubService.getClubById(this.clubId),
        members: this.clubService.getClubMembers(this.clubId)
      })
      .pipe(
        takeUntil(this.destroy$),
        switchMap(result => {
          // Extraire tous les IDs des utilisateurs
          const userIds = [...new Set(result.members.map(m => m.userId))];
          
          if (userIds.length === 0) {
            return of({ ...result, users: [] });
          }

          // Charger les informations des utilisateurs
          return this.userService.getUsersByIds(userIds).pipe(
            map(users => ({ ...result, users }))
          );
        })
      )
      .subscribe({
        next: (result) => {
          console.log('✅ Club data loaded:', result);
          this.club = result.club;
          
          // Créer une map des utilisateurs
          const userMap = new Map(result.users.map(u => [u.id, u]));
          
          // Enrichir les membres avec les noms et emails des utilisateurs
          this.members = result.members.map(member => {
            const user = userMap.get(member.userId);
            return {
              ...member,
              userName: user ? `${user.firstName} ${user.lastName}` : undefined,
              userEmail: user?.email,
              userPhoto: user?.image
            };
          });
          
          this.cdr.markForCheck();
          resolve();
        },
        error: (err) => {
          console.error('❌ Error loading club data:', err);
          this.error = 'Failed to load club details';
          reject(err);
        }
      });
    });
  }

  /**
   * Initialiser WebSocket
   */
  private async initializeWebSocket() {
    try {
      console.log('🔌 Connecting to WebSocket...');
      
      // Connecter au WebSocket
      await this.clubWsService.connect();

      // Surveiller le statut de connexion
      this.clubWsService.getConnectionStatus()
        .pipe(takeUntil(this.destroy$))
        .subscribe(status => {
          this.isConnected = status;
          console.log(status ? '✅ WebSocket connected' : '❌ WebSocket disconnected');
          this.cdr.markForCheck();
        });

      // S'abonner aux notifications du club
      this.clubWsService.subscribeToClub(this.clubId);

      console.log('✅ WebSocket initialized successfully');
    } catch (error) {
      console.error('❌ Failed to initialize WebSocket:', error);
      this.showErrorToast('Unable to connect to real-time updates');
    }
  }

  /**
   * Configurer l'auto-synchronisation des données
   */
  private setupAutoSync() {
    console.log('🔄 Setting up auto-sync...');

    // Auto-refresh quand les données du club changent
    this.dataSyncService.onClubDataChanged()
      .pipe(takeUntil(this.destroy$))
      .subscribe(change => {
        if (change.clubId === this.clubId || !change.clubId) {
          console.log('🔄 Auto-refreshing club data due to:', change.action);
          
          switch (change.action) {
            case 'CLUB_UPDATED':
            case 'CLUB_APPROVED':
            case 'CLUB_ACTIVATED':
            case 'CLUB_SUSPENDED':
              this.refreshClubData();
              break;
            
            case 'CLUB_DELETED':
              this.handleClubDeleted();
              break;
          }
        }
      });

    // Auto-refresh quand les membres changent
    this.dataSyncService.onMemberDataChanged()
      .pipe(takeUntil(this.destroy$))
      .subscribe(change => {
        if (change && change.clubId === this.clubId) {
          console.log('🔄 Auto-refreshing members due to:', change.action);
          this.refreshMembers();
          
          // Aussi rafraîchir le club pour mettre à jour le compteur
          if (change.action === 'JOINED' || change.action === 'LEFT') {
            this.refreshClubData();
          }
        }
      });
  }

  /**
   * Rafraîchir les données du club
   */
  private refreshClubData() {
    console.log('🔄 Refreshing club data...');
    
    this.clubService.getClubById(this.clubId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (club) => {
          console.log('✅ Club data refreshed');
          this.club = club;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('❌ Error refreshing club:', err);
        }
      });
  }

  /**
   * Rafraîchir la liste des membres
   */
  private refreshMembers() {
    console.log('🔄 Refreshing members list...');
    
    this.clubService.getClubMembers(this.clubId)
      .pipe(
        takeUntil(this.destroy$),
        switchMap(members => {
          // Extraire tous les IDs des utilisateurs
          const userIds = [...new Set(members.map(m => m.userId))];
          
          if (userIds.length === 0) {
            return of({ members, users: [] });
          }

          // Charger les informations des utilisateurs
          return this.userService.getUsersByIds(userIds).pipe(
            map(users => ({ members, users }))
          );
        })
      )
      .subscribe({
        next: ({ members, users }) => {
          console.log('✅ Members list refreshed:', members.length, 'members');
          
          // Créer une map des utilisateurs
          const userMap = new Map(users.map(u => [u.id, u]));
          
          // Enrichir les membres avec les noms et emails des utilisateurs
          this.members = members.map(member => {
            const user = userMap.get(member.userId);
            return {
              ...member,
              userName: user ? `${user.firstName} ${user.lastName}` : undefined,
              userEmail: user?.email,
              userPhoto: user?.image
            };
          });
          
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('❌ Error refreshing members:', err);
        }
      });
  }

  /**
   * Gérer la suppression du club
   */
  private handleClubDeleted() {
    Swal.fire({
      title: 'Club Deleted',
      text: 'This club has been deleted',
      icon: 'warning',
      confirmButtonText: 'Go to Clubs List'
    }).then(() => {
      this.router.navigate(['/dashboard/clubs']);
    });
  }

  /**
   * Rafraîchir manuellement toutes les données
   */
  refreshAll() {
    console.log('🔄 Manual refresh triggered');
    this.loading = true;
    
    this.loadAllData().then(() => {
      this.loading = false;
      this.showSuccessToast('Data refreshed successfully');
    }).catch(() => {
      this.loading = false;
      this.showErrorToast('Failed to refresh data');
    });
  }

  /**
   * Supprimer le club
   */
  deleteClub() {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This action cannot be undone!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clubService.deleteClub(this.clubId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Pas besoin de rediriger ici, le WebSocket le fera
              console.log('Club deleted, waiting for WebSocket notification...');
            },
            error: (err) => {
              console.error('Error deleting club:', err);
              Swal.fire('Error!', 'Failed to delete club.', 'error');
            }
          });
      }
    });
  }

  /**
   * Retirer un membre
   */
  removeMember(userId: number) {
    Swal.fire({
      title: 'Remove member?',
      text: 'Are you sure you want to remove this member?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, remove!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clubService.leaveClub(this.clubId, userId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Les données se rafraîchiront automatiquement via WebSocket
              console.log('Member removed, waiting for auto-refresh...');
            },
            error: (err) => {
              console.error('Error removing member:', err);
              this.showErrorToast('Failed to remove member');
            }
          });
      }
    });
  }

  /**
   * Obtenir la classe CSS pour le badge de catégorie
   */
  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100',
      'BOOK': 'text-green-800 bg-green-100',
      'DRAMA': 'text-orange-800 bg-orange-100',
      'WRITING': 'text-purple-800 bg-purple-100'
    };
    return classes[category] || 'text-gray-800 bg-gray-100';
  }

  /**
   * Obtenir les initiales d'un nom pour l'avatar placeholder
   */
  getInitials(name?: string): string {
    if (!name) return '?';
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  // ============================================
  // Méthodes de notification Toast
  // ============================================

  private showSuccessToast(message: string) {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
    });

    Toast.fire({
      icon: 'success',
      title: message
    });
  }

  private showErrorToast(message: string) {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 5000,
      timerProgressBar: true,
    });

    Toast.fire({
      icon: 'error',
      title: message
    });
  }
}
