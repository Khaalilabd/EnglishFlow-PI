import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { PackService } from '../../../core/services/pack.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { PackEnrollment } from '../../../core/models/pack-enrollment.model';
import { CourseCategory } from '../../../core/models/course-category.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-my-packs',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-packs.component.html',
  styleUrls: ['./my-packs.component.scss']
})
export class MyPacksComponent implements OnInit {
  enrollments: PackEnrollment[] = [];
  categories: CourseCategory[] = [];
  
  loading = false;
  
  message = '';
  messageType: 'success' | 'error' = 'success';

  constructor(
    private packEnrollmentService: PackEnrollmentService,
    private packService: PackService,
    private categoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadMyEnrollments();
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadMyEnrollments(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;
    this.packEnrollmentService.getByStudentId(currentUser.id).subscribe({
      next: (enrollments) => {
        // Verify that packs still exist
        if (enrollments.length === 0) {
          this.enrollments = [];
          this.loading = false;
          return;
        }

        // Check each pack to see if it still exists
        const packChecks = enrollments.map(enrollment => 
          this.packService.getById(enrollment.packId).pipe(
            catchError(() => of(null)) // Return null if pack doesn't exist
          )
        );

        forkJoin(packChecks).subscribe({
          next: (packs) => {
            // Filter out enrollments where pack no longer exists
            this.enrollments = enrollments.filter((enrollment, index) => packs[index] !== null);
            this.loading = false;
          },
          error: (error) => {
            console.error('Error verifying packs:', error);
            // Show all enrollments even if verification fails
            this.enrollments = enrollments;
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.showMessage('Failed to load your packs', 'error');
        this.loading = false;
      }
    });
  }

  viewPackDetails(enrollment: PackEnrollment): void {
    // Navigate to pack details page
    this.router.navigate(['/pack-details', enrollment.packId]);
  }

  continueLearning(enrollment: PackEnrollment): void {
    // Navigate to course learning page (relative to current route)
    this.router.navigate(['../pack', enrollment.packId, 'learning'], { relativeTo: this.route });
  }

  getCategoryIcon(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.icon || '📚';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.color || '#3B82F6';
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return 'bg-green-500';
    if (progress >= 50) return 'bg-blue-500';
    if (progress >= 25) return 'bg-yellow-500';
    return 'bg-gray-400';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }
}
