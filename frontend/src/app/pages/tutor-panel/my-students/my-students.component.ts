import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { PackService } from '../../../core/services/pack.service';
import { AuthService } from '../../../core/services/auth.service';
import { PackEnrollment } from '../../../core/models/pack-enrollment.model';
import { Pack } from '../../../core/models/pack.model';

@Component({
  selector: 'app-my-students',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-students.component.html',
  styleUrls: ['./my-students.component.scss']
})
export class MyStudentsComponent implements OnInit {
  enrollments: PackEnrollment[] = [];
  packs: Pack[] = [];
  selectedPackId: number | null = null;
  filteredEnrollments: PackEnrollment[] = [];
  
  loading = true;
  
  // Stats
  totalStudents = 0;
  activeStudents = 0;
  completedStudents = 0;
  averageProgress = 0;

  constructor(
    private packEnrollmentService: PackEnrollmentService,
    private packService: PackService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTutorData();
  }

  loadTutorData(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;

    // Load tutor's packs
    this.packService.getByTutorId(currentUser.id).subscribe({
      next: (packs) => {
        this.packs = packs;
        
        // Load enrollments for this tutor
        this.loadEnrollments(currentUser.id);
      },
      error: (error) => {
        console.error('Error loading packs:', error);
        this.loading = false;
      }
    });
  }

  loadEnrollments(tutorId: number): void {
    this.packEnrollmentService.getByTutorId(tutorId).subscribe({
      next: (enrollments) => {
        // Filter enrollments to only show those for packs that still exist
        const packIds = this.packs.map(p => p.id);
        this.enrollments = enrollments.filter(e => packIds.includes(e.packId));
        this.filteredEnrollments = this.enrollments;
        this.calculateStats();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.loading = false;
      }
    });
  }

  calculateStats(): void {
    this.totalStudents = this.enrollments.length;
    this.activeStudents = this.enrollments.filter(e => e.status === 'ACTIVE').length;
    this.completedStudents = this.enrollments.filter(e => e.status === 'COMPLETED').length;
    
    if (this.enrollments.length > 0) {
      const totalProgress = this.enrollments.reduce((sum, e) => sum + (e.progressPercentage || 0), 0);
      this.averageProgress = Math.round(totalProgress / this.enrollments.length);
    }
  }

  filterByPack(packId: number | null): void {
    this.selectedPackId = packId;
    
    if (packId === null) {
      this.filteredEnrollments = this.enrollments;
    } else {
      this.filteredEnrollments = this.enrollments.filter(e => e.packId === packId);
    }
  }

  getPackEnrollmentCount(packId: number): number {
    return this.enrollments.filter(e => e.packId === packId).length;
  }

  getPackName(packId: number): string {
    const pack = this.packs.find(p => p.id === packId);
    return pack?.name || 'Unknown Pack';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'status-active';
      case 'COMPLETED':
        return 'status-completed';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#3b82f6';
    if (progress >= 25) return '#f59e0b';
    return '#6b7280';
  }

  viewStudentProgress(enrollment: PackEnrollment): void {
    // Navigate to detailed student progress view (to be created)
    alert(`View progress for ${enrollment.studentName}\nProgress: ${enrollment.progressPercentage}%\nCompleted: ${enrollment.completedCourses}/${enrollment.totalCourses} courses`);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }
}
