import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { Lesson, LessonType } from '../../../core/models/lesson.model';
import { Chapter } from '../../../core/models/chapter.model';

@Component({
  selector: 'app-lesson-view',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './lesson-view.component.html',
  styleUrls: ['./lesson-view.component.scss']
})
export class LessonViewComponent implements OnInit {
  lesson: Lesson | null = null;
  chapter: Chapter | null = null;
  chapterLessons: Lesson[] = [];
  loading = false;
  error: string | null = null;
  LessonType = LessonType;
  safeVideoUrl: SafeResourceUrl | null = null;

  constructor(
    private route: ActivatedRoute,
    private lessonService: LessonService,
    private chapterService: ChapterService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const lessonId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLesson(lessonId);
  }

  loadLesson(lessonId: number): void {
    this.loading = true;
    this.error = null;
    
    this.lessonService.getLessonById(lessonId).subscribe({
      next: (lesson) => {
        // Map contentUrl to videoUrl and fileUrl based on type
        this.lesson = {
          ...lesson,
          type: lesson.lessonType,
          videoUrl: lesson.lessonType === LessonType.VIDEO ? lesson.contentUrl : undefined,
          fileUrl: lesson.lessonType === LessonType.FILE ? lesson.contentUrl : undefined
        };
        if (this.lesson.videoUrl) {
          this.safeVideoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.lesson.videoUrl);
        }
        this.loadChapter(lesson.chapterId);
        this.loadChapterLessons(lesson.chapterId);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load lesson';
        this.loading = false;
        console.error('Error loading lesson:', err);
      }
    });
  }

  loadChapter(chapterId: number): void {
    this.chapterService.getChapterById(chapterId).subscribe({
      next: (chapter) => {
        this.chapter = chapter;
      },
      error: (err) => console.error('Error loading chapter:', err)
    });
  }

  loadChapterLessons(chapterId: number): void {
    this.lessonService.getLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.chapterLessons = lessons.map(l => ({
          ...l,
          type: l.lessonType
        })).sort((a, b) => a.orderIndex - b.orderIndex);
      },
      error: (err) => console.error('Error loading chapter lessons:', err)
    });
  }

  navigateToLesson(lessonId: number): void {
    this.loadLesson(lessonId);
  }

  getNextLesson(): Lesson | null {
    if (!this.lesson || this.chapterLessons.length === 0) return null;
    const currentIndex = this.chapterLessons.findIndex(l => l.id === this.lesson!.id);
    return currentIndex < this.chapterLessons.length - 1 ? this.chapterLessons[currentIndex + 1] : null;
  }

  getPreviousLesson(): Lesson | null {
    if (!this.lesson || this.chapterLessons.length === 0) return null;
    const currentIndex = this.chapterLessons.findIndex(l => l.id === this.lesson!.id);
    return currentIndex > 0 ? this.chapterLessons[currentIndex - 1] : null;
  }
}
