import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Chapter } from '../../../core/models/chapter.model';
import { Lesson, LessonType } from '../../../core/models/lesson.model';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonService } from '../../../core/services/lesson.service';

@Component({
  selector: 'app-chapter-details',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './chapter-details.component.html',
  styleUrls: ['./chapter-details.component.scss']
})
export class ChapterDetailsComponent implements OnInit {
  chapter: Chapter | null = null;
  lessons: Lesson[] = [];
  loading = true;
  showLessonModal = false;
  showDeleteModal = false;
  lessonForm: FormGroup;
  editingLesson: Lesson | null = null;
  deletingLesson: Lesson | null = null;
  lessonTypes = Object.values(LessonType);

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private chapterService: ChapterService,
    private lessonService: LessonService,
    private fb: FormBuilder
  ) {
    this.lessonForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      content: [''],
      contentUrl: [''],
      lessonType: [LessonType.TEXT, Validators.required],
      orderIndex: [1, [Validators.required, Validators.min(1)]],
      duration: [0, [Validators.min(0)]],
      isPreview: [false],
      isPublished: [true]
    });
  }

  ngOnInit(): void {
    const chapterId = this.route.snapshot.paramMap.get('id');
    if (chapterId) {
      this.loadChapter(Number(chapterId));
      this.loadLessons(Number(chapterId));
    }
  }

  loadChapter(chapterId: number): void {
    this.chapterService.getChapterById(chapterId).subscribe({
      next: (chapter) => {
        this.chapter = chapter;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading chapter:', error);
        this.loading = false;
      }
    });
  }

  loadLessons(chapterId: number): void {
    this.lessonService.getLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.lessons = lessons.sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
      },
      error: (error) => {
        console.error('Error loading lessons:', error);
      }
    });
  }

  openLessonModal(lesson?: Lesson): void {
    this.editingLesson = lesson || null;
    if (lesson) {
      this.lessonForm.patchValue({
        title: lesson.title,
        description: lesson.description,
        content: lesson.content,
        contentUrl: lesson.contentUrl,
        lessonType: lesson.lessonType,
        orderIndex: lesson.orderIndex,
        duration: lesson.duration,
        isPreview: lesson.isPreview,
        isPublished: lesson.isPublished
      });
    } else {
      this.lessonForm.reset({
        lessonType: LessonType.TEXT,
        orderIndex: this.lessons.length + 1,
        duration: 0,
        isPreview: false,
        isPublished: true
      });
    }
    this.showLessonModal = true;
  }

  closeLessonModal(): void {
    this.showLessonModal = false;
    this.editingLesson = null;
    this.lessonForm.reset();
  }

  saveLesson(): void {
    if (this.lessonForm.invalid || !this.chapter?.id) return;

    const lessonData: Lesson = {
      ...this.lessonForm.value,
      chapterId: this.chapter.id
    };

    const request = this.editingLesson?.id
      ? this.lessonService.updateLesson(this.editingLesson.id, lessonData)
      : this.lessonService.createLesson(lessonData);

    request.subscribe({
      next: () => {
        this.loadLessons(this.chapter!.id!);
        this.closeLessonModal();
      },
      error: (error) => {
        console.error('Error saving lesson:', error);
        alert('Failed to save lesson');
      }
    });
  }

  openDeleteModal(lesson: Lesson): void {
    this.deletingLesson = lesson;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deletingLesson = null;
  }

  confirmDeleteLesson(): void {
    if (this.deletingLesson?.id && this.chapter?.id) {
      this.lessonService.deleteLesson(this.deletingLesson.id).subscribe({
        next: () => {
          this.loadLessons(this.chapter!.id!);
          this.closeDeleteModal();
        },
        error: (error) => {
          console.error('Error deleting lesson:', error);
          alert('Failed to delete lesson');
        }
      });
    }
  }

  getLessonTypeIcon(type: LessonType): string {
    const icons: Record<LessonType, string> = {
      [LessonType.VIDEO]: 'M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z',
      [LessonType.TEXT]: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      [LessonType.FILE]: 'M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z',
      [LessonType.IMAGE]: 'M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z',
      [LessonType.MIXED]: 'M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10'
    };
    return icons[type] || icons[LessonType.TEXT];
  }

  getLessonTypeColor(type: LessonType): string {
    const colors: Record<LessonType, string> = {
      [LessonType.VIDEO]: 'bg-red-100 text-red-700',
      [LessonType.TEXT]: 'bg-blue-100 text-blue-700',
      [LessonType.FILE]: 'bg-green-100 text-green-700',
      [LessonType.IMAGE]: 'bg-pink-100 text-pink-700',
      [LessonType.MIXED]: 'bg-purple-100 text-purple-700'
    };
    return colors[type] || colors[LessonType.TEXT];
  }

  goBack(): void {
    if (this.chapter?.courseId) {
      this.router.navigate(['/dashboard/courses', this.chapter.courseId]);
    } else {
      this.router.navigate(['/dashboard/courses']);
    }
  }
}
