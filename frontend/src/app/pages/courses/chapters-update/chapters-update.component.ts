import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ChapterService } from '../../../core/services/chapter.service';
import { CourseService } from '../../../core/services/course.service';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-chapters-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './chapters-update.component.html'
})
export class ChaptersUpdateComponent implements OnInit {
  chapterForm: FormGroup;
  courses: Course[] = [];
  chapterId!: number;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private chapterService: ChapterService,
    private courseService: CourseService,
    private route: ActivatedRoute,
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
    this.chapterId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCourses();
    this.loadChapter();
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (data) => this.courses = data,
      error: (err) => console.error('Error loading courses:', err)
    });
  }

  loadChapter(): void {
    this.chapterService.getChapterById(this.chapterId).subscribe({
      next: (chapter) => {
        this.chapterForm.patchValue(chapter);
        // Mark form as pristine after loading to detect changes
        this.chapterForm.markAsPristine();
        this.chapterForm.markAsUntouched();
      },
      error: (err) => {
        this.error = 'Failed to load chapter';
        console.error('Error loading chapter:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {
      this.loading = true;
      this.error = null;
      this.chapterService.updateChapter(this.chapterId, this.chapterForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/chapters']);
        },
        error: (err) => {
          this.error = 'Failed to update chapter';
          this.loading = false;
          console.error('Error updating chapter:', err);
        }
      });
    }
  }
}
