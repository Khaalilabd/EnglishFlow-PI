import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { Chapter } from '../../../core/models/chapter.model';
import { LessonType } from '../../../core/models/lesson.model';

@Component({
  selector: 'app-lessons-update',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './lessons-update.component.html'
})
export class LessonsUpdateComponent implements OnInit {
  lessonForm: FormGroup;
  chapters: Chapter[] = [];
  lessonTypes = Object.values(LessonType);
  lessonId!: number;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private lessonService: LessonService,
    private chapterService: ChapterService,
    private route: ActivatedRoute,
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
    this.lessonId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadChapters();
    this.loadLesson();
  }

  loadChapters(): void {
    this.chapterService.getAllChapters().subscribe({
      next: (data) => this.chapters = data,
      error: (err) => console.error('Error loading chapters:', err)
    });
  }

  loadLesson(): void {
    this.lessonService.getLessonById(this.lessonId).subscribe({
      next: (lesson) => {
        this.lessonForm.patchValue(lesson);
      },
      error: (err) => {
        this.error = 'Failed to load lesson';
        console.error('Error loading lesson:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.lessonForm.valid) {
      this.loading = true;
      this.error = null;
      this.lessonService.updateLesson(this.lessonId, this.lessonForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/lessons']);
        },
        error: (err) => {
          this.error = 'Failed to update lesson';
          this.loading = false;
          console.error('Error updating lesson:', err);
        }
      });
    }
  }
}
