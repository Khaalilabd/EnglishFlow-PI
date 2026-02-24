import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PackService } from '../../core/services/pack.service';
import { CourseService } from '../../core/services/course.service';
import { CourseCategoryService } from '../../core/services/course-category.service';
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
  mobileMenuOpen = false;
  isAuthenticated$;
  currentUser$;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private packService: PackService,
    private courseService: CourseService,
    private categoryService: CourseCategoryService,
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
    }
  }

  loadPackDetails(packId: number): void {
    this.loading = true;
    this.packService.getById(packId).subscribe({
      next: (pack) => {
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

  loadCourses(courseIds: number[]): void {
    if (!courseIds || courseIds.length === 0) {
      this.loading = false;
      return;
    }

    // Load each course
    const courseRequests = courseIds.map(id => 
      this.courseService.getCourseById(id).toPromise()
    );

    Promise.all(courseRequests).then(courses => {
      this.courses = courses.filter(c => c !== undefined) as Course[];
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

    // Navigate to pack catalog for enrollment
    this.router.navigate(['/student-panel/pack-catalog']);
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
