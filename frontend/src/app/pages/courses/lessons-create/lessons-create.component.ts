import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { Chapter } from '../../../core/models/chapter.model';
import { LessonType } from '../../../core/models/lesson.model';

@Component({
  selector: 'app-lessons-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './lessons-create.component.html'
})
export class LessonsCreateComponent implements OnInit {
  lessonForm: FormGroup;
  chapters: Chapter[] = [];
  lessonTypes = Object.values(LessonType);
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private lessonService: LessonService,
    private chapterService: ChapterService,
    private router: Router
  ) {
    this.lessonForm = this.fb.group({
      title: ['', Validators.required],
      chapterId: ['', Validators.required],
      type: ['', Validators.required],
      orderIndex: [0, [Validators.required, Validators.min(0)]],
      content: [''],
      videoUrl: [''],
      fileUrl: [''],
      duration: [0, Validators.min(0)]
    });
  }

  ngOnInit(): void {
    this.loadChapters();
  }

  loadChapters(): void {
    this.chapterService.getAllChapters().subscribe({
      next: (data) => this.chapters = data,
      error: (err) => console.error('Error loading chapters:', err)
    });
  }

  onSubmit(): void {
    if (this.lessonForm.valid) {
      this.loading = true;
      this.error = null;
      this.lessonService.createLesson(this.lessonForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/lessons']);
        },
        error: (err) => {
          this.error = 'Failed to create lesson';
          this.loading = false;
          console.error('Error creating lesson:', err);
        }
      });
    }
  }
}
