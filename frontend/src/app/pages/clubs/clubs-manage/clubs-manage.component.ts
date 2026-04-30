import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { ClubService } from '../../../core/services/club.service';
import { UserService } from '../../../core/services/user.service';
import { Club, Member } from '../../../core/models/club.model';
import { NotificationService } from '../../../core/services/notification.service';
import { ClubWebSocketService } from '../../../services/club-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';
import { forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-clubs-manage',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './clubs-manage.component.html',
  styleUrl: './clubs-manage.component.scss'
})
export class ClubsManageComponent implements OnInit, OnDestroy {
  clubs: Club[] = [];
  filteredClubs: Club[] = [];
  selectedClub: Club | null = null;
  clubMembers: Member[] = [];
  loading = false;
  loadingMembers = false;
  error: string | null = null;
  showMembersModal = false;
  showMembersSection = true;
  showSuspendModal = false;
  clubToSuspend: Club | null = null;
  suspensionReason = '';
  currentUserId = 1;

  // Search / Filter / Sort
  searchQuery = '';
  filterCategory = '';
  filterStatus = '';
  sortBy: 'date_asc' | 'date_desc' | 'name_asc' | 'name_desc' = 'date_desc';
  
  private wsSubscriptions = new Subscription();

  constructor(
    private clubService: ClubService,
    private userService: UserService,
    private notificationService: NotificationService,
    private clubWsService: ClubWebSocketService,
    private dataSyncService: DataSyncService
  ) {}

  ngOnInit() {
    this.initializeWebSocket();
    this.setupAutoSync();
    this.loadClubs();
  }
  
  ngOnDestroy() {
    this.wsSubscriptions.unsubscribe();
    this.clubWsService.disconnect();
  }
  
  private async initializeWebSocket() {
    try {
      await this.clubWsService.connect();
      this.clubWsService.subscribeToGlobalClubs();
      console.log('✅ Club WebSocket initialized for clubs-manage');
    } catch (error) {
      console.error('❌ Failed to initialize WebSocket:', error);
    }
  }
  
  private setupAutoSync() {
    const syncSub = this.dataSyncService.onClubDataChanged().subscribe(change => {
      if (change.action !== 'none') {
        console.log('🔄 Club data changed in clubs-manage:', change.action);
        this.loadClubs();
      }
    });
    this.wsSubscriptions.add(syncSub);
  }

  loadClubs() {
    this.loading = true;
    this.error = null;

    // Charger tous les clubs approuvés
    this.clubService.getApprovedClubs().pipe(
      switchMap(clubs => {
        // Extraire tous les IDs des créateurs
        const creatorIds = [...new Set(clubs.map(c => c.createdBy).filter(id => id !== undefined))] as number[];
        
        if (creatorIds.length === 0) {
          return of({ clubs, users: [] });
        }

        // Charger les informations des créateurs
        return this.userService.getUsersByIds(creatorIds).pipe(
          map(users => ({ clubs, users }))
        );
      })
    ).subscribe({
      next: ({ clubs, users }) => {
        // Créer une map des utilisateurs
        const userMap = new Map(users.map(u => [u.id, u]));
        
        // Enrichir les clubs avec les noms des créateurs
        this.clubs = clubs.map(club => {
          const creator = userMap.get(club.createdBy!);
          return {
            ...club,
            creatorName: creator ? `${creator.firstName} ${creator.lastName}` : undefined
          };
        });
        
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading clubs:', err);
        this.error = 'Failed to load clubs. Please try again.';
        this.loading = false;
      }
    });
  }

  applyFilters() {
    let result = [...this.clubs];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(c =>
        c.name?.toLowerCase().includes(q) ||
        c.description?.toLowerCase().includes(q) ||
        c.creatorName?.toLowerCase().includes(q)
      );
    }

    if (this.filterCategory) {
      result = result.filter(c => c.category === this.filterCategory);
    }

    if (this.filterStatus) {
      result = result.filter(c => c.status === this.filterStatus);
    }

    result.sort((a, b) => {
      switch (this.sortBy) {
        case 'date_asc':  return new Date(a.createdAt || '').getTime() - new Date(b.createdAt || '').getTime();
        case 'date_desc': return new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime();
        case 'name_asc':  return (a.name || '').localeCompare(b.name || '');
        case 'name_desc': return (b.name || '').localeCompare(a.name || '');
        default: return 0;
      }
    });

    this.filteredClubs = result;
  }

  resetFilters() {
    this.searchQuery = '';
    this.filterCategory = '';
    this.filterStatus = '';
    this.sortBy = 'date_desc';
    this.applyFilters();
  }

  viewClubDetails(club: Club) {
    this.selectedClub = club;
    this.loadClubMembers(club.id!);
    this.showMembersModal = true;
  }

  loadClubMembers(clubId: number) {
    this.loadingMembers = true;
    this.clubService.getClubMembers(clubId).pipe(
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
    ).subscribe({
      next: ({ members, users }) => {
        // Créer une map des utilisateurs
        const userMap = new Map(users.map(u => [u.id, u]));
        
        // Enrichir les membres avec les noms des utilisateurs
        this.clubMembers = members.map(member => {
          const user = userMap.get(member.userId);
          return {
            ...member,
            userName: user ? `${user.firstName} ${user.lastName}` : undefined,
            userEmail: user?.email
          };
        });
        
        this.loadingMembers = false;
      },
      error: (err) => {
        console.error('Error loading club members:', err);
        this.loadingMembers = false;
      }
    });
  }

  closeModal() {
    this.showMembersModal = false;
    this.selectedClub = null;
    this.clubMembers = [];
    this.showMembersSection = true; // Réinitialiser à ouvert
  }

  toggleMembersSection() {
    this.showMembersSection = !this.showMembersSection;
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'ACADEMIC': 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300',
      'SPORTS': 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300',
      'ARTS': 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300',
      'TECHNOLOGY': 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900 dark:text-indigo-300',
      'SOCIAL': 'bg-pink-100 text-pink-800 dark:bg-pink-900 dark:text-pink-300',
      'OTHER': 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
    };
    return classes[category] || classes['OTHER'];
  }

  getRankBadgeClass(rank: string): string {
    const classes: { [key: string]: string } = {
      'PRESIDENT': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
      'VICE_PRESIDENT': 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-300',
      'MEMBER': 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
    };
    return classes[rank] || classes['MEMBER'];
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric'
    });
  }

  openSuspendModal(club: Club) {
    this.clubToSuspend = club;
    this.suspensionReason = '';
    this.showSuspendModal = true;
  }

  closeSuspendModal() {
    this.showSuspendModal = false;
    this.clubToSuspend = null;
    this.suspensionReason = '';
  }

  suspendClub() {
    if (!this.clubToSuspend || !this.suspensionReason.trim()) {
      this.notificationService.warning('Missing Reason', 'Please provide a reason for suspension');
      return;
    }

    this.clubService.suspendClub(this.clubToSuspend.id!, this.currentUserId, this.suspensionReason).subscribe({
      next: () => {
        this.notificationService.success('Club Suspended', 'Club has been suspended successfully!');
        this.closeSuspendModal();
        this.loadClubs();
      },
      error: (err: any) => {
        let errorMessage = 'Failed to suspend club. ';
        if (err.status === 400) {
          errorMessage += 'The club might not be in APPROVED status or the request is invalid.';
        } else if (err.error?.message) {
          errorMessage += err.error.message;
        } else {
          errorMessage += 'Please try again.';
        }
        this.notificationService.error('Suspension Failed', errorMessage);
      }
    });
  }

  activateClub(club: Club) {
    if (confirm('Are you sure you want to activate this club?')) {
      this.clubService.activateClub(club.id!, this.currentUserId).subscribe({
        next: () => {
          this.notificationService.success('Club Activated', 'Club has been activated successfully!');
          this.loadClubs();
        },
        error: (err: any) => {
          let errorMessage = 'Failed to activate club. ';
          if (err.status === 400) {
            errorMessage += 'The club might not be in SUSPENDED status.';
          } else if (err.error?.message) {
            errorMessage += err.error.message;
          } else {
            errorMessage += 'Please try again.';
          }
          this.notificationService.error('Activation Failed', errorMessage);
        }
      });
    }
  }

  deleteClub(club: Club) {
    if (confirm(`Are you sure you want to permanently delete "${club.name}"? This action cannot be undone.`)) {
      this.clubService.deleteClub(club.id!).subscribe({
        next: () => {
          this.notificationService.success('Club Deleted', 'Club has been deleted successfully!');
          this.loadClubs();
        },
        error: (err: any) => {
          let errorMessage = 'Failed to delete club. ';
          if (err.error?.message) {
            errorMessage += err.error.message;
          } else {
            errorMessage += 'Please try again.';
          }
          this.notificationService.error('Delete Failed', errorMessage);
        }
      });
    }
  }
}
