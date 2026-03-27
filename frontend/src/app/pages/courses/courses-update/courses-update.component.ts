import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Course, UpdateCourseRequest, CourseStatus, CEFR_LEVELS } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';

@Component({
  selector: 'app-courses-update',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses-update.component.html',
  styleUrls: ['./courses-update.component.scss']
})
export class CoursesUpdateComponent implements OnInit {
  course: UpdateCourseRequest | null = null;
  courseId: number | null = null;
  loading = true;
  levels = CEFR_LEVELS;
  statuses = Object.values(CourseStatus);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.courseId = Number(id);
      this.loadCourse(this.courseId);
    }
  }

  loadCourse(id: number): void {
    this.courseService.getCourseById(id).subscribe({
      next: (course) => {
        this.course = {
          title: course.title,
          description: course.description,
          category: course.category,
          level: course.level,
          maxStudents: course.maxStudents,
          schedule: course.schedule,
          duration: course.duration,
          tutorId: course.tutorId,
          fileUrl: course.fileUrl,
          status: course.status
        };
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading course:', error);
        alert('Course not found');
        this.router.navigate(['/dashboard/courses']);
      }
    });
  }

  onSubmit(): void {
    if (this.course && this.courseId) {
      this.courseService.updateCourse(this.courseId, this.course).subscribe({
        next: (updatedCourse) => {
          console.log('Course updated:', updatedCourse);
          alert('Course updated successfully!');
          this.router.navigate(['/dashboard/courses']);
        },
        error: (error) => {
          console.error('Error updating course:', error);
          alert('Error updating course. Please try again.');
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard/courses']);
  }
}

