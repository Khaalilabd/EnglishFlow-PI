import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ChapterService } from '../../../core/services/chapter.service';
import { CourseService } from '../../../core/services/course.service';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-chapters-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './chapters-create.component.html'
})
export class ChaptersCreateComponent implements OnInit {
  chapterForm: FormGroup;
  courses: Course[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private chapterService: ChapterService,
    private courseService: CourseService,
    private router: Router
  ) {
    this.chapterForm = this.fb.group({
      title: ['', Validators.required],
      courseId: ['', Validators.required],
      orderIndex: [0, [Validators.required, Validators.min(0)]],
      objectives: ['']
    });
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (data) => this.courses = data,
      error: (err) => console.error('Error loading courses:', err)
    });
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {
      this.loading = true;
      this.error = null;
      this.chapterService.createChapter(this.chapterForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/chapters']);
        },
        error: (err) => {
          this.error = 'Failed to create chapter';
          this.loading = false;
          console.error('Error creating chapter:', err);
        }
      });
    }
  }
}
