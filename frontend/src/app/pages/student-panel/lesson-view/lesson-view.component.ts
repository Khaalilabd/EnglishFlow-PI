import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonMediaService } from '../../../core/services/lesson-media.service';
import { Lesson, LessonType } from '../../../core/models/lesson.model';
import { Chapter } from '../../../core/models/chapter.model';
import { LessonMedia } from '../../../core/models/lesson-media.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-lesson-view',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './lesson-view.component.html',
  styleUrls: ['./lesson-view.component.scss']
})
export class LessonViewComponent implements OnInit {
  lesson: Lesson | null = null;
  chapter: Chapter | null = null;
  chapterLessons: Lesson[] = [];
  mediaItems: LessonMedia[] = [];
  loading = false;
  error: string | null = null;
  LessonType = LessonType;
  safeVideoUrl: SafeResourceUrl | null = null;
  safeFileUrl: SafeResourceUrl | null = null;
  visiblePdfIds: Set<number> = new Set();
  editingMediaId: number | null = null;
  editingMediaTitle: string = '';

  constructor(
    private route: ActivatedRoute,
    private lessonService: LessonService,
    private chapterService: ChapterService,
    private lessonMediaService: LessonMediaService,
    private sanitizer: DomSanitizer
  ) {}

  getSafeHtml(html: string) {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  ngOnInit(): void {
    const lessonId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLesson(lessonId);
  }

  loadLesson(lessonId: number): void {
    this.loading = true;
    this.error = null;
    
    this.lessonService.getLessonById(lessonId).subscribe({
      next: (lesson) => {
        this.lesson = lesson;
        
        // Load media items for this lesson
        this.loadMediaItems(lessonId);
        
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

  loadMediaItems(lessonId: number): void {
    this.lessonMediaService.getMediaByLesson(lessonId).subscribe({
      next: (items) => {
        this.mediaItems = items.sort((a, b) => a.position - b.position);
      },
      error: (err) => console.error('Error loading media items:', err)
    });
  }

  getSafeUrl(url: string): SafeResourceUrl {
    const fullUrl = this.getFullUrl(url);
    if (this.isYouTubeOrVimeo(url)) {
      return this.sanitizer.bypassSecurityTrustResourceUrl(this.getEmbedUrl(fullUrl));
    }
    return this.sanitizer.bypassSecurityTrustResourceUrl(fullUrl);
  }

  togglePdfViewer(id: number): void {
    if (this.visiblePdfIds.has(id)) {
      this.visiblePdfIds.delete(id);
    } else {
      this.visiblePdfIds.add(id);
    }
  }

  isPdfVisible(id: number): boolean {
    return this.visiblePdfIds.has(id);
  }

  startEditingMedia(media: LessonMedia): void {
    this.editingMediaId = media.id!;
    this.editingMediaTitle = media.title || '';
  }

  cancelEditingMedia(): void {
    this.editingMediaId = null;
    this.editingMediaTitle = '';
  }

  saveMediaTitle(media: LessonMedia): void {
    if (!this.editingMediaTitle.trim()) {
      return;
    }

    const updatedMedia: LessonMedia = {
      ...media,
      title: this.editingMediaTitle.trim()
    };

    this.lessonMediaService.updateMedia(media.id!, updatedMedia).subscribe({
      next: () => {
        media.title = this.editingMediaTitle.trim();
        this.editingMediaId = null;
        this.editingMediaTitle = '';
      },
      error: (err) => console.error('Error updating media title:', err)
    });
  }

  isEditingMedia(id: number): boolean {
    return this.editingMediaId === id;
  }

  getFullUrl(url: string): string {
    if (!url) return '';
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    // If URL starts with /api/, replace it with the full API URL
    if (url.startsWith('/api/')) {
      return `http://localhost:8082${url}`;
    }
    // Otherwise append to API URL
    return `${environment.apiUrl}${url.startsWith('/') ? url : '/' + url}`;
  }

  getEmbedUrl(url: string): string {
    // Convert YouTube watch URLs to embed URLs
    if (url.includes('youtube.com/watch')) {
      const videoId = url.split('v=')[1]?.split('&')[0];
      return `https://www.youtube.com/embed/${videoId}`;
    }
    // Convert YouTube short URLs to embed URLs
    if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1]?.split('?')[0];
      return `https://www.youtube.com/embed/${videoId}`;
    }
    // Convert Vimeo URLs to embed URLs
    if (url.includes('vimeo.com/')) {
      const videoId = url.split('vimeo.com/')[1]?.split('?')[0];
      return `https://player.vimeo.com/video/${videoId}`;
    }
    return url;
  }

  isYouTubeOrVimeo(url: string): boolean {
    return url.includes('youtube.com') || url.includes('youtu.be') || url.includes('vimeo.com');
  }

  isPDF(url: string): boolean {
    return url.toLowerCase().endsWith('.pdf') || url.includes('pdf');
  }

  isImage(url: string): boolean {
    return /\.(jpg|jpeg|png|gif|webp)$/i.test(url);
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
