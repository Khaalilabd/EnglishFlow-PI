import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { PackService } from '../../../core/services/pack.service';
import { CourseService } from '../../../core/services/course.service';
import { AuthService } from '../../../core/services/auth.service';
import { PackEnrollment } from '../../../core/models/pack-enrollment.model';
import { Pack } from '../../../core/models/pack.model';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-pack-courses',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pack-courses.component.html',
  styleUrls: ['./pack-courses.component.scss']
})
export class PackCoursesComponent implements OnInit {
  packId!: number;
  enrollment: PackEnrollment | null = null;
  pack: Pack | null = null;
  courses: Course[] = [];
  
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private packEnrollmentService: PackEnrollmentService,
    private packService: PackService,
    private courseService: CourseService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.packId = +this.route.snapshot.paramMap.get('packId')!;
    this.loadPackData();
  }

  loadPackData(): void {
    this.loading = true;
    const user = this.authService.currentUserValue;
    
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    // Load enrollment
    this.packEnrollmentService.getByStudentId(user.id).subscribe({
      next: (enrollments) => {
        this.enrollment = enrollments.find(e => e.packId === this.packId) || null;
        
        if (!this.enrollment) {
          alert('You are not enrolled in this pack');
          this.router.navigate(['/user-panel/my-packs']);
          return;
        }

        // Load pack details
        this.loadPack();
      },
      error: (error) => {
        console.error('Error loading enrollment:', error);
        this.loading = false;
      }
    });
  }

  loadPack(): void {
    this.packService.getById(this.packId).subscribe({
      next: (pack) => {
        this.pack = pack;
        this.loadCourses(pack.courseIds);
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

  startCourse(course: Course): void {
    // Navigate to course learning page with packId as query param
    // From /user-panel/pack/:packId/learning to /user-panel/course/:courseId/learning
    // Need to go up 3 levels: learning -> :packId -> pack -> user-panel, then down to course
    this.router.navigate(['../../../course', course.id, 'learning'], { 
      relativeTo: this.route,
      queryParams: { packId: this.packId }
    });
  }

  getCourseProgress(courseId: number): number {
    // TODO: Get actual progress from backend
    return 0;
  }

  goBack(): void {
    this.router.navigate(['/user-panel/my-packs']);
  }
}
