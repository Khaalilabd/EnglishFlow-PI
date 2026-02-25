import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PackService } from '../../core/services/pack.service';
import { CourseService } from '../../core/services/course.service';
import { CourseCategoryService } from '../../core/services/course-category.service';
import { PackEnrollmentService } from '../../core/services/pack-enrollment.service';
import { AuthService } from '../../core/services/auth.service';
import { Pack } from '../../core/models/pack.model';
import { Course } from '../../core/models/course.model';
import { CourseCategory } from '../../core/models/course-category.model';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-pack-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FrontofficeUserDropdownComponent, FrontofficeNotificationDropdownComponent],
  templateUrl: './pack-details.component.html',
  styleUrl: './pack-details.component.scss'
})
export class PackDetailsComponent implements OnInit {
  pack: Pack | null = null;
  courses: Course[] = [];
  category: CourseCategory | null = null;
  loading = true;
  enrolling = false;
  isEnrolled = false;
  checkingEnrollment = false;
  mobileMenuOpen = false;
  isAuthenticated$;
  currentUser$;
  showEnrollModal = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private packService: PackService,
    private courseService: CourseService,
    private categoryService: CourseCategoryService,
    private enrollmentService: PackEnrollmentService,
    public authService: AuthService
  ) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(
      map(user => !!user)
    );
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {
    const packId = this.route.snapshot.paramMap.get('id');
    if (packId) {
      this.loadPackDetails(+packId);
      this.checkIfEnrolled(+packId);
    }
  }

  loadPackDetails(packId: number): void {
    this.loading = true;
    this.packService.getById(packId).subscribe({
      next: (pack) => {
        console.log('Pack loaded:', pack);
        console.log('Course IDs:', pack.courseIds);
        this.pack = pack;
        this.loadCourses(pack.courseIds);
        this.loadCategory(pack.category);
      },
      error: (error) => {
        console.error('Error loading pack:', error);
        this.loading = false;
      }
    });
  }

  checkIfEnrolled(packId: number): void {
    const user = this.authService.currentUserValue;
    if (!user || !user.id) return;

    this.checkingEnrollment = true;
    this.enrollmentService.isStudentEnrolled(user.id, packId).subscribe({
      next: (enrolled) => {
        this.isEnrolled = enrolled;
        this.checkingEnrollment = false;
      },
      error: (error) => {
        console.error('Error checking enrollment:', error);
        this.checkingEnrollment = false;
      }
    });
  }

  loadCourses(courseIds: number[]): void {
    console.log('loadCourses called with:', courseIds);
    
    if (!courseIds || courseIds.length === 0) {
      console.log('No course IDs found, skipping course loading');
      this.loading = false;
      return;
    }

    console.log('Loading courses for IDs:', courseIds);
    
    // Load each course
    const courseRequests = courseIds.map(id => {
      console.log('Fetching course with ID:', id);
      return this.courseService.getCourseById(id).toPromise();
    });

    Promise.all(courseRequests).then(courses => {
      console.log('Courses loaded:', courses);
      this.courses = courses.filter(c => c !== undefined) as Course[];
      console.log('Filtered courses:', this.courses);
      this.loading = false;
    }).catch(error => {
      console.error('Error loading courses:', error);
      this.loading = false;
    });
  }

  loadCategory(categoryName: string): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.category = categories.find(c => c.name === categoryName) || null;
      },
      error: (error) => {
        console.error('Error loading category:', error);
      }
    });
  }

  getEnrollmentPercentage(): number {
    if (!this.pack || !this.pack.maxStudents || this.pack.maxStudents === 0) return 0;
    const enrolled = this.pack.maxStudents - (this.pack.availableSlots || 0);
    return Math.round((enrolled / this.pack.maxStudents) * 100);
  }

  getCategoryColor(): string {
    return this.category?.color || '#3B82F6';
  }

  getCategoryIcon(): string {
    return this.category?.icon || '📚';
  }

  enrollInPack(): void {
    const user = this.authService.currentUserValue;
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    // Check if user is a student
    if (user.role !== 'STUDENT') {
      alert('Only students can enroll in packs');
      return;
    }

    // Show confirmation modal
    this.showEnrollModal = true;
  }

  confirmEnrollment(): void {
    const user = this.authService.currentUserValue;
    if (!user || !user.id || !this.pack || !this.pack.id) return;

    this.enrolling = true;
    this.enrollmentService.enrollStudent(user.id, this.pack.id).subscribe({
      next: (enrollment) => {
        console.log('Enrollment successful:', enrollment);
        this.enrolling = false;
        this.showEnrollModal = false;
        this.isEnrolled = true;
        
        // Update available slots
        if (this.pack && this.pack.availableSlots) {
          this.pack.availableSlots--;
        }

        // Show success message
        alert('🎉 Enrollment successful! You can now access this pack in "My Packs".');
        
        // Redirect to My Packs
        setTimeout(() => {
          this.router.navigate(['/user-panel/my-packs']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error enrolling:', error);
        this.enrolling = false;
        this.showEnrollModal = false;
        
        // Show detailed error message
        let errorMessage = '❌ Enrollment failed. ';
        
        if (error.status === 400) {
          // Try to get the error message from backend
          if (error.error && typeof error.error === 'string') {
            errorMessage += error.error;
          } else if (error.error && error.error.message) {
            errorMessage += error.error.message;
          } else {
            errorMessage += 'Possible reasons:\n';
            errorMessage += '- You may already be enrolled in this pack\n';
            errorMessage += '- The pack may be full\n';
            errorMessage += '- Enrollment period may not be open yet\n';
            errorMessage += `- Start date: ${this.pack?.enrollmentStartDate ? new Date(this.pack.enrollmentStartDate).toLocaleDateString() : 'Not set'}\n`;
            errorMessage += `- End date: ${this.pack?.enrollmentEndDate ? new Date(this.pack.enrollmentEndDate).toLocaleDateString() : 'Not set'}`;
          }
        } else {
          errorMessage += 'An unexpected error occurred. Please try again later.';
        }
        
        alert(errorMessage);
      }
    });
  }

  cancelEnrollment(): void {
    this.showEnrollModal = false;
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
