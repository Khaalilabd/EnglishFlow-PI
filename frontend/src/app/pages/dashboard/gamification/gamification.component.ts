import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GamificationService, UserLevel } from '../../../services/gamification.service';
import { UserRoleBadgeComponent } from '../../../shared/components/user-role-badge/user-role-badge.component';

@Component({
  selector: 'app-gamification',
  standalone: true,
  imports: [CommonModule, FormsModule, UserRoleBadgeComponent],
  templateUrl: './gamification.component.html',
  styleUrls: ['./gamification.component.css']
})
export class GamificationComponent implements OnInit {
  userLevels: UserLevel[] = [];
  filteredLevels: UserLevel[] = [];
  globalStats: any = null;
  isLoading = true;
  searchTerm = '';
  filterStatus = 'all'; // all, not-assessed, assessed, certified
  
  // Modal states
  showUpdateModal = false;
  showCertifyModal = false;
  selectedUser: UserLevel | null = null;
  selectedLevel = 'A1';
  
  englishLevels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

  constructor(private gamificationService: GamificationService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.gamificationService.getAllUserLevels().subscribe({
      next: (levels) => {
        this.userLevels = levels;
        this.applyFilters();
        this.loadStats();
      },
      error: (err) => {
        console.error('Failed to load user levels:', err);
        this.isLoading = false;
      }
    });
  }

  loadStats() {
    this.gamificationService.getGlobalStats().subscribe({
      next: (stats) => {
        this.globalStats = stats;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load stats:', err);
        this.isLoading = false;
      }
    });
  }

  applyFilters() {
    let filtered = [...this.userLevels];
    
    // Filter by status
    if (this.filterStatus === 'not-assessed') {
      filtered = filtered.filter(u => !u.hasCompletedAssessment);
    } else if (this.filterStatus === 'assessed') {
      filtered = filtered.filter(u => u.hasCompletedAssessment && !u.certifiedLevel);
    } else if (this.filterStatus === 'certified') {
      filtered = filtered.filter(u => u.certifiedLevel);
    }
    
    // Filter by search term
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(u => 
        u.userId.toString().includes(term)
      );
    }
    
    this.filteredLevels = filtered;
  }

  openUpdateModal(user: UserLevel) {
    this.selectedUser = user;
    this.selectedLevel = user.assessmentLevel || 'A1';
    this.showUpdateModal = true;
  }

  openCertifyModal(user: UserLevel) {
    this.selectedUser = user;
    this.selectedLevel = user.assessmentLevel || 'A1';
    this.showCertifyModal = true;
  }

  updateAssessment() {
    if (!this.selectedUser) return;
    
    this.gamificationService.adminUpdateAssessment(this.selectedUser.userId, this.selectedLevel).subscribe({
      next: () => {
        this.showUpdateModal = false;
        this.loadData();
      },
      error: (err) => console.error('Failed to update assessment:', err)
    });
  }

  certifyLevel() {
    if (!this.selectedUser) return;
    
    this.gamificationService.adminCertifyLevel(this.selectedUser.userId, this.selectedLevel).subscribe({
      next: () => {
        this.showCertifyModal = false;
        this.loadData();
      },
      error: (err) => console.error('Failed to certify level:', err)
    });
  }

  revokeCertification(userId: number) {
    if (!confirm('Are you sure you want to revoke this certification?')) return;
    
    this.gamificationService.adminRevokeCertification(userId).subscribe({
      next: () => this.loadData(),
      error: (err) => console.error('Failed to revoke certification:', err)
    });
  }

  closeModal() {
    this.showUpdateModal = false;
    this.showCertifyModal = false;
    this.selectedUser = null;
  }
}
