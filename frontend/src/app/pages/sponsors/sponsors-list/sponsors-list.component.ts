import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SponsorService } from '../../../core/services/sponsor.service';
import { Sponsor, SponsorLevel, CreateSponsorRequest } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';
import { SponsorWebSocketService } from '../../../services/sponsor-websocket.service';
import { DataSyncService } from '../../../services/data-sync.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-sponsors-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sponsors-list.component.html',
  styleUrl: './sponsors-list.component.scss'
})
export class SponsorsListComponent implements OnInit, OnDestroy {
  sponsors: Sponsor[] = [];
  filteredSponsors: Sponsor[] = [];
  loading = false;
  showModal = false;
  isEditMode = false;
  viewMode: 'grid' | 'list' = 'grid'; // View mode toggle
  
  currentSponsor: CreateSponsorRequest = this.getEmptySponsor();
  searchTerm: string = '';
  selectedLevel: string = '';
  
  SponsorLevel = SponsorLevel;
  
  private wsSubscription?: Subscription;
  private dataSyncSubscription?: Subscription;

  // Details popup
  showDetailsPopup = false;
  detailsSponsor: Sponsor | null = null;
  sponsoredClubs: Sponsor[] = [];
  loadingClubs = false;

  constructor(
    private readonly sponsorService: SponsorService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly sponsorWsService: SponsorWebSocketService,
    private readonly dataSyncService: DataSyncService,
    private readonly http: HttpClient
  ) {}

  async ngOnInit() {
    this.loadSponsors();
    await this.initializeWebSocket();
    this.setupAutoSync();
  }
  
  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    if (this.dataSyncSubscription) {
      this.dataSyncSubscription.unsubscribe();
    }
    this.sponsorWsService.disconnect();
  }
  
  private async initializeWebSocket() {
    try {
      await this.sponsorWsService.connect();
      this.sponsorWsService.subscribeToSponsors();
      
      this.wsSubscription = this.sponsorWsService.getSponsorNotifications().subscribe(
        (notification) => {
          if (notification) {
            this.handleSponsorNotification(notification);
          }
        }
      );
      
      console.log('✅ Sponsor WebSocket initialized');
    } catch (error) {
      console.error('Failed to initialize WebSocket:', error);
    }
  }
  
  private setupAutoSync() {
    this.dataSyncSubscription = this.dataSyncService.onSponsorDataChanged().subscribe(
      (change) => {
        if (change.action !== 'none') {
          console.log('🔄 Auto-sync triggered, reloading sponsors:', change.action);
          this.loadSponsors(true); // Recharger les sponsors quand il y a un changement
        }
      }
    );
  }
  
  private handleSponsorNotification(notification: any) {
    console.log('🔔 Handling sponsor notification:', notification.type, notification);
    
    // Toujours recharger les données pour être sûr d'avoir la dernière version
    this.loadSponsors(true);
  }

  loadSponsors(forceRefresh: boolean = false) {
    this.loading = true;
    this.sponsorService.getAllSponsors(forceRefresh).subscribe({
      next: (sponsors) => {
        this.sponsors = sponsors;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading sponsors:', err);
        this.notificationService.error('Error', 'Failed to load sponsors');
        this.loading = false;
      }
    });
  }

  applyFilters() {
    this.filteredSponsors = this.sponsors.filter(sponsor => {
      // Only show main sponsor profiles (not club sponsorship requests)
      if (sponsor.clubId) return false;

      const matchesSearch = !this.searchTerm || 
        sponsor.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (sponsor.contactEmail && sponsor.contactEmail.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      const matchesLevel = !this.selectedLevel || sponsor.level === this.selectedLevel;
      
      return matchesSearch && matchesLevel;
    });
  }

  getCountByLevel(level: string): number {
    return this.sponsors.filter(s => s.level === level && !s.clubId).length;
  }

  getTotalContribution(): number {
    return this.sponsors.filter(s => !s.clubId).reduce((sum, s) => sum + (s.contributionAmount || 0), 0);
  }

  toggleViewMode() {
    this.viewMode = this.viewMode === 'grid' ? 'list' : 'grid';
  }

  getLevelBadgeClass(level?: SponsorLevel): string {
    const classes: { [key: string]: string } = {
      'GOLD': 'gold',
      'SILVER': 'silver',
      'BRONZE': 'bronze'
    };
    return level ? classes[level] : classes['BRONZE'];
  }

  viewDetails(sponsor: Sponsor) {
    this.router.navigate(['/dashboard/sponsors/detail', sponsor.id]);
  }

  editSponsor(sponsor: Sponsor) {
    this.isEditMode = true;
    this.currentSponsor = {
      name: sponsor.name,
      description: sponsor.description,
      logo: sponsor.logo,
      website: sponsor.website,
      contactEmail: sponsor.contactEmail,
      contactPhone: sponsor.contactPhone,
      level: sponsor.level,
      contributionAmount: sponsor.contributionAmount
    };
    this.showModal = true;
  }

  createSponsor() {
    this.isEditMode = false;
    this.currentSponsor = this.getEmptySponsor();
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.currentSponsor = this.getEmptySponsor();
  }

  onLogoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (!file.type.startsWith('image/')) {
        this.notificationService.warning('Invalid File', 'Please select an image file');
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.notificationService.warning('File Too Large', 'Image size must be less than 5MB');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.currentSponsor.logo = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeLogo() {
    this.currentSponsor.logo = '';
  }

  saveSponsor() {
    if (!this.currentSponsor.name) {
      this.notificationService.warning('Missing Fields', 'Name is required');
      return;
    }

    if (this.isEditMode) {
      const sponsor = this.sponsors.find(s => 
        s.name === this.currentSponsor.name || 
        (s.contactEmail && s.contactEmail === this.currentSponsor.contactEmail)
      );
      
      if (sponsor && sponsor.id) {
        this.sponsorService.updateSponsor(sponsor.id, this.currentSponsor).subscribe({
          next: () => {
            this.notificationService.success('Success', 'Sponsor updated successfully');
            this.closeModal();
            this.loadSponsors(true); // Force refresh after update
          },
          error: (err) => {
            console.error('Error updating sponsor:', err);
            this.notificationService.error('Error', 'Failed to update sponsor');
          }
        });
      }
    } else {
      this.sponsorService.createSponsor(this.currentSponsor).subscribe({
        next: () => {
          this.notificationService.success('Success', 'Sponsor created successfully');
          this.closeModal();
          this.loadSponsors(true); // Force refresh after create
        },
        error: (err) => {
          console.error('Error creating sponsor:', err);
          this.notificationService.error('Error', 'Failed to create sponsor');
        }
      });
    }
  }

  private getEmptySponsor(): CreateSponsorRequest {
    return {
      name: '',
      description: '',
      logo: '',
      website: '',
      contactEmail: '',
      contactPhone: '',
      level: SponsorLevel.BRONZE,
      contributionAmount: 0
    };
  }

  openDetails(sponsor: Sponsor) {
    this.detailsSponsor = sponsor;
    this.sponsoredClubs = [];
    this.showDetailsPopup = true;

    if (!sponsor.userId) return;
    this.loadingClubs = true;
    this.http.get<Sponsor[]>(`${environment.apiUrl}/sponsors/user/${sponsor.userId}`).subscribe({
      next: (all) => {
        this.sponsoredClubs = all.filter(s => s.clubId);
        this.loadingClubs = false;
      },
      error: () => { this.loadingClubs = false; }
    });
  }

  get totalSponsoredClubs(): number {
    return this.sponsoredClubs.reduce((sum, s) => sum + (s.contributionAmount || 0), 0);
  }

  closeDetails() {
    this.showDetailsPopup = false;
    this.detailsSponsor = null;
    this.sponsoredClubs = [];
  }

  getStatusClass(status?: string): string {
    const map: Record<string, string> = {
      PENDING:  'bg-yellow-100 text-yellow-700',
      APPROVED: 'bg-green-100 text-green-700',
      REJECTED: 'bg-red-100 text-red-700'
    };
    return map[status || ''] || 'bg-gray-100 text-gray-600';
  }

  deleteSponsor(sponsor: Sponsor) {
    if (confirm(`Are you sure you want to delete ${sponsor.name}?`)) {
      this.sponsorService.deleteSponsor(sponsor.id!).subscribe({
        next: () => {
          this.notificationService.success('Success', 'Sponsor deleted successfully');
          this.loadSponsors(true); // Force refresh after delete
        },
        error: (err) => {
          console.error('Error deleting sponsor:', err);
          this.notificationService.error('Error', 'Failed to delete sponsor');
        }
      });
    }
  }
}
