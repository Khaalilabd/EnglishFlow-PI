import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PackService } from '../../../core/services/pack.service';
import { CourseService } from '../../../core/services/course.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { AuthService } from '../../../core/services/auth.service';
import { Pack } from '../../../core/models/pack.model';
import { Course } from '../../../core/models/course.model';
import { CourseCategory } from '../../../core/models/course-category.model';
import { PaymentModalComponent } from '../../../shared/components/payment-modal/payment-modal.component';

@Component({
  selector: 'app-student-pack-details',
  standalone: true,
  imports: [CommonModule, RouterModule, PaymentModalComponent],
  templateUrl: './student-pack-details.component.html',
  styleUrls: ['./student-pack-details.component.scss']
})
export class StudentPackDetailsComponent implements OnInit {
  pack: Pack | null = null;
  courses: Course[] = [];
  category: CourseCategory | null = null;
  loading = true;
  enrolling = false;
  isEnrolled = false;
  checkingEnrollment = false;

  // Payment modal
  showPaymentModal = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private packService: PackService,
    private courseService: CourseService,
    private categoryService: CourseCategoryService,
    private enrollmentService: PackEnrollmentService,
    private authService: AuthService
  ) {}

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
        this.pack = pack;
        this.loadCourses(pack.courseIds);
        this.loadCategory(pack.category);
      },
      error: () => { this.loading = false; }
    });
  }

  checkIfEnrolled(packId: number): void {
    const user = this.authService.currentUserValue;
    if (!user?.id) return;
    this.checkingEnrollment = true;
    this.enrollmentService.isStudentEnrolled(user.id, packId).subscribe({
      next: (enrolled) => { this.isEnrolled = enrolled; this.checkingEnrollment = false; },
      error: () => { this.checkingEnrollment = false; }
    });
  }

  loadCourses(courseIds: number[]): void {
    if (!courseIds?.length) { this.loading = false; return; }
    Promise.all(courseIds.map(id => this.courseService.getCourseById(id).toPromise()))
      .then(courses => { this.courses = courses.filter(Boolean) as Course[]; this.loading = false; })
      .catch(() => { this.loading = false; });
  }

  loadCategory(categoryName: string): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (cats) => { this.category = cats.find(c => c.name === categoryName) || null; },
      error: () => {}
    });
  }

  enrollInPack(): void {
    const user = this.authService.currentUserValue;
    if (!user?.id || !this.pack?.id) return;
    if (user.role !== 'STUDENT') { alert('Only students can enroll in packs'); return; }
    this.showPaymentModal = true;
  }

  onPaymentModalClosed(): void {
    this.showPaymentModal = false;
  }

  onEnrolled(): void {
    this.isEnrolled = true;
    this.showPaymentModal = false;
    setTimeout(() => this.router.navigate(['/user-panel/my-packs']), 1500);
  }

  goToMyPacks(): void { this.router.navigate(['/user-panel/my-packs']); }
  goBack(): void { this.router.navigate(['/user-panel/pack-catalog']); }
  getCategoryColor(): string { return this.category?.color || '#3B82F6'; }
  getCategoryIcon(): string { return this.category?.icon || '­ƒôÜ'; }

  getEnrollmentPercentage(): number {
    if (!this.pack?.maxStudents) return 0;
    return Math.round(((this.pack.currentEnrolledStudents || 0) / this.pack.maxStudents) * 100);
  }

  getTotalDuration(): number {
    return this.courses.reduce((t, c) => t + ((c as any).estimatedDuration || 0), 0);
  }

  getTotalLessons(): number {
    return this.courses.reduce((t, c) => {
      const chapters = (c as any).chapters || [];
      return t + chapters.reduce((ct: number, ch: any) => ct + (ch.lessons?.length || 0), 0);
    }, 0);
  }

  getCourseDuration(course: Course): number { return (course as any).estimatedDuration || 0; }
  getCourseChaptersCount(course: Course): number { return ((course as any).chapters || []).length; }
}
