import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { PackEnrollment } from '../../../core/models/pack-enrollment.model';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-my-packs',
  standalone: true,
  imports: [CommonModule],
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
    private categoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router
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
        this.enrollments = enrollments;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.showMessage('Failed to load your packs', 'error');
        this.loading = false;
      }
    });
  }

  viewPackDetails(enrollment: PackEnrollment): void {
    this.router.navigate(['/user-panel/packs', enrollment.packId]);
  }

  continueLearning(enrollment: PackEnrollment): void {
    // Navigate to the pack's courses
    this.router.navigate(['/user-panel/packs', enrollment.packId, 'courses']);
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
