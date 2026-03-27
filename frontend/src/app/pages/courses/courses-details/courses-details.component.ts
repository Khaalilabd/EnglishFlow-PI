import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CdkDragDrop, moveItemInArray, DragDropModule } from '@angular/cdk/drag-drop';
import { Course } from '../../../core/models/course.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Lesson, LessonType } from '../../../core/models/lesson.model';
import { LessonMedia } from '../../../core/models/lesson-media.model';
import { CourseService } from '../../../core/services/course.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonService } from '../../../core/services/lesson.service';
import { LessonMediaService } from '../../../core/services/lesson-media.service';
import { FileUploadService } from '../../../core/services/file-upload.service';

@Component({
  selector: 'app-courses-details',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, FormsModule, DragDropModule],
  templateUrl: './courses-details.component.html',
  styleUrls: ['./courses-details.component.scss']
})
export class CoursesDetailsComponent implements OnInit {
  course: Course | null = null;
  chapters: Chapter[] = [];
  lessonsByChapter: Map<number, Lesson[]> = new Map();
  loading = true;
  
  showChapterModal = false;
  chapterForm: FormGroup;
  editingChapter: Chapter | null = null;
  deletingChapter: Chapter | null = null;
  
  showLessonModal = false;
  lessonForm: FormGroup;
  editingLesson: Lesson | null = null;
  deletingLesson: Lesson | null = null;
  selectedChapterId: number | null = null;
  
  showDeleteModal = false;
  deleteType: 'chapter' | 'lesson' = 'chapter';
  lessonTypes = Object.values(LessonType);
  LessonType = LessonType; // Expose enum to template
  expandedChapters: Set<number> = new Set();
  
  uploadingFile = false;
  uploadProgress = 0;
  selectedFile: File | null = null;
  
  lessonMediaItems: LessonMedia[] = [];
  currentMediaType: LessonType = LessonType.VIDEO;
  showMediaModal = false;
  mediaForm: FormGroup;
  newMediaUrl: string = '';
  
  editingMediaId: number | null = null;
  editingMediaTitle: string = '';
  
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private chapterService: ChapterService,
    private lessonService: LessonService,
    private lessonMediaService: LessonMediaService,
    private fileUploadService: FileUploadService,
    private fb: FormBuilder
  ) {
    this.chapterForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      objectives: [''],
      orderIndex: [1, [Validators.required, Validators.min(1)]],
      estimatedDuration: [0, [Validators.min(0)]],
      isPublished: [true]
    });

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
    
    this.mediaForm = this.fb.group({
      url: [''],
      mediaType: [LessonType.VIDEO, Validators.required],
      title: [''],
      description: ['']
    });
  }

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    if (courseId) {
      this.loadCourse(Number(courseId));
      this.loadChapters(Number(courseId));
    }
  }

  loadCourse(courseId: number): void {
    this.courseService.getCourseById(courseId).subscribe({
      next: (course) => {
        this.course = course;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading course:', error);
        this.loading = false;
      }
    });
  }

  loadChapters(courseId: number): void {
    this.chapterService.getChaptersByCourse(courseId).subscribe({
      next: (chapters) => {
        this.chapters = chapters.sort((a, b) => a.orderIndex - b.orderIndex);
        chapters.forEach(chapter => {
          if (chapter.id) {
            this.loadLessons(chapter.id);
            this.expandedChapters.add(chapter.id);
          }
        });
      },
      error: (error) => console.error('Error loading chapters:', error)
    });
  }

  loadLessons(chapterId: number): void {
    this.lessonService.getLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.lessonsByChapter.set(chapterId, lessons.sort((a, b) => a.orderIndex - b.orderIndex));
      },
      error: (error) => {
        console.error(`Error loading lessons for chapter ${chapterId}:`, error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
      }
    });
  }

  toggleChapter(chapterId: number): void {
    if (this.expandedChapters.has(chapterId)) {
      this.expandedChapters.delete(chapterId);
    } else {
      this.expandedChapters.add(chapterId);
    }
  }

  isChapterExpanded(chapterId: number): boolean {
    return this.expandedChapters.has(chapterId);
  }

  getChapterLessons(chapterId: number): Lesson[] {
    const lessons = this.lessonsByChapter.get(chapterId) || [];
    return lessons;
  }

  openChapterModal(chapter?: Chapter): void {
    this.editingChapter = chapter || null;
    if (chapter) {
      this.chapterForm.patchValue({
        title: chapter.title,
        description: chapter.description,
        objectives: chapter.objectives?.join(', ') || '',
        orderIndex: chapter.orderIndex,
        estimatedDuration: chapter.estimatedDuration || 0,
        isPublished: chapter.isPublished
      });
    } else {
      this.chapterForm.reset({
        orderIndex: this.chapters.length + 1,
        isPublished: true
      });
    }
    this.showChapterModal = true;
  }

  closeChapterModal(): void {
    this.showChapterModal = false;
    this.editingChapter = null;
    this.chapterForm.reset();
  }

  saveChapter(): void {
    if (this.chapterForm.invalid || !this.course?.id) return;

    const formValue = this.chapterForm.value;
    const chapterData: Chapter = {
      ...formValue,
      objectives: formValue.objectives ? formValue.objectives.split(',').map((o: string) => o.trim()) : [],
      courseId: this.course.id
    };

    if (this.editingChapter?.id) {
      this.chapterService.updateChapter(this.editingChapter.id, chapterData).subscribe({
        next: () => {
          this.loadChapters(this.course!.id!);
          this.closeChapterModal();
        },
        error: (error) => console.error('Error updating chapter:', error)
      });
    } else {
      this.chapterService.createChapter(chapterData).subscribe({
        next: () => {
          this.loadChapters(this.course!.id!);
          this.closeChapterModal();
        },
        error: (error) => console.error('Error creating chapter:', error)
      });
    }
  }

  openDeleteChapterModal(chapter: Chapter): void {
    this.deletingChapter = chapter;
    this.deleteType = 'chapter';
    this.showDeleteModal = true;
  }

  confirmDeleteChapter(): void {
    if (this.deletingChapter?.id) {
      this.chapterService.deleteChapter(this.deletingChapter.id).subscribe({
        next: () => {
          this.loadChapters(this.course!.id!);
          this.closeDeleteModal();
        },
        error: (error) => console.error('Error deleting chapter:', error)
      });
    }
  }

  openLessonModal(chapterId: number, lesson?: Lesson): void {
    this.selectedChapterId = chapterId;
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
      if (lesson.id) {
        this.loadMediaItems(lesson.id);
      }
    } else {
      const lessons = this.getChapterLessons(chapterId);
      this.lessonForm.reset({
        lessonType: LessonType.TEXT,
        orderIndex: lessons.length + 1,
        isPreview: false,
        isPublished: true
      });
      this.lessonMediaItems = [];
    }
    this.showLessonModal = true;
  }

  closeLessonModal(): void {
    this.showLessonModal = false;
    this.editingLesson = null;
    this.selectedFile = null;
    this.uploadProgress = 0;
    this.newMediaUrl = '';
    this.lessonMediaItems = [];
    this.lessonForm.reset();
  }

  onLessonTypeChange(): void {
    this.lessonMediaItems = [];
    this.newMediaUrl = '';
  }

  getAcceptedFileTypes(): string {
    const lessonType = this.lessonForm.get('lessonType')?.value;
    switch (lessonType) {
      case LessonType.VIDEO: return 'video/*';
      case LessonType.DOCUMENT: return '.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,image/*';
      default: return '*';
    }
  }

  addMediaItemToLesson(): void {
    if (this.selectedFile) {
      this.uploadMediaFile();
    } else if (this.newMediaUrl) {
      const mediaType = this.lessonForm.get('lessonType')?.value;
      const mediaItem: LessonMedia = {
        url: this.newMediaUrl,
        mediaType: mediaType,
        title: '',
        description: '',
        position: this.lessonMediaItems.length,
        lessonId: 0 // Will be set when lesson is saved
      };
      this.lessonMediaItems.push(mediaItem);
      this.newMediaUrl = '';
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    this.uploadingFile = true;
    this.fileUploadService.uploadFileWithProgress(this.selectedFile).subscribe({
      next: (event) => {
        this.uploadProgress = event.progress;
        if (event.response) {
          this.lessonForm.patchValue({ contentUrl: event.response.url });
          this.uploadingFile = false;
          this.selectedFile = null;
        }
      },
      error: (error) => {
        console.error('Upload error:', error);
        this.uploadingFile = false;
        alert('Failed to upload file');
      }
    });
  }

  removeFile(): void {
    this.selectedFile = null;
    this.uploadProgress = 0;
    this.lessonForm.patchValue({ contentUrl: '' });
  }

  saveLesson(): void {
    if (this.lessonForm.invalid || !this.selectedChapterId) return;

    const lessonData: Lesson = {
      ...this.lessonForm.value,
      chapterId: this.selectedChapterId
    };

    const saveLessonAndMedia = (lessonId: number) => {
      // Save media items for this lesson
      if (this.lessonMediaItems.length > 0) {
        this.lessonMediaItems.forEach((media, index) => {
          const mediaWithLesson = { ...media, lessonId, position: index };
          if (media.id) {
            this.lessonMediaService.updateMedia(media.id, mediaWithLesson).subscribe();
          } else {
            this.lessonMediaService.createMedia(mediaWithLesson).subscribe();
          }
        });
      }
      this.loadLessons(this.selectedChapterId!);
      this.closeLessonModal();
    };

    if (this.editingLesson?.id) {
      this.lessonService.updateLesson(this.editingLesson.id, lessonData).subscribe({
        next: () => saveLessonAndMedia(this.editingLesson!.id!),
        error: (error) => console.error('Error updating lesson:', error)
      });
    } else {
      this.lessonService.createLesson(lessonData).subscribe({
        next: (created) => saveLessonAndMedia(created.id!),
        error: (error) => console.error('Error creating lesson:', error)
      });
    }
  }

  openDeleteLessonModal(lesson: Lesson): void {
    this.deletingLesson = lesson;
    this.deleteType = 'lesson';
    this.showDeleteModal = true;
  }

  confirmDeleteLesson(): void {
    if (this.deletingLesson?.id) {
      this.lessonService.deleteLesson(this.deletingLesson.id).subscribe({
        next: () => {
          this.loadLessons(this.deletingLesson!.chapterId);
          this.closeDeleteModal();
        },
        error: (error) => console.error('Error deleting lesson:', error)
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deletingChapter = null;
    this.deletingLesson = null;
  }

  confirmDelete(): void {
    if (this.deleteType === 'chapter') {
      this.confirmDeleteChapter();
    } else {
      this.confirmDeleteLesson();
    }
  }

  getLessonTypeIcon(type: LessonType): string {
    const icons: Record<LessonType, string> = {
      [LessonType.VIDEO]: 'M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      [LessonType.TEXT]: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      [LessonType.DOCUMENT]: 'M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z',
      [LessonType.QUIZ]: 'M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      [LessonType.ASSIGNMENT]: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01',
      [LessonType.INTERACTIVE]: 'M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
    };
    return icons[type] || icons[LessonType.TEXT];
  }

  getLessonTypeColor(type: LessonType): string {
    const colors: Record<LessonType, string> = {
      [LessonType.VIDEO]: 'bg-red-100 text-red-700',
      [LessonType.TEXT]: 'bg-blue-100 text-blue-700',
      [LessonType.DOCUMENT]: 'bg-green-100 text-green-700',
      [LessonType.QUIZ]: 'bg-yellow-100 text-yellow-700',
      [LessonType.ASSIGNMENT]: 'bg-pink-100 text-pink-700',
      [LessonType.INTERACTIVE]: 'bg-purple-100 text-purple-700'
    };
    return colors[type] || colors[LessonType.TEXT];
  }

  goBack(): void {
    this.router.navigate(['/dashboard/courses']);
  }

  editCourse(): void {
    if (this.course?.id) {
      this.router.navigate(['/dashboard/courses', this.course.id, 'edit']);
    }
  }
  
  openMediaModal(lessonId: number): void {
    this.selectedChapterId = lessonId;
    this.loadMediaItems(lessonId);
    this.showMediaModal = true;
  }
  
  closeMediaModal(): void {
    this.showMediaModal = false;
    this.mediaForm.reset({ mediaType: LessonType.VIDEO });
    this.selectedFile = null;
    this.uploadProgress = 0;
  }
  
  loadMediaItems(lessonId: number): void {
    this.lessonMediaService.getMediaByLesson(lessonId).subscribe({
      next: (items) => {
        this.lessonMediaItems = items.sort((a, b) => a.position - b.position);
      },
      error: (error) => console.error('Error loading media items:', error)
    });
  }
  
  addMediaItem(): void {
    if (!this.selectedChapterId) return;
    
    const mediaType = this.mediaForm.get('mediaType')?.value;
    this.currentMediaType = mediaType;
    
    if (this.selectedFile) {
      this.uploadMediaFile();
    } else {
      const url = this.mediaForm.get('url')?.value;
      if (url) {
        this.saveMediaItem(url);
      }
    }
  }
  
  uploadMediaFile(): void {
    if (!this.selectedFile) return;
    
    this.uploadingFile = true;
    this.fileUploadService.uploadFileWithProgress(this.selectedFile).subscribe({
      next: (event) => {
        this.uploadProgress = event.progress;
        if (event.response) {
          const mediaType = this.lessonForm.get('lessonType')?.value;
          const mediaItem: LessonMedia = {
            url: event.response.url,
            mediaType: mediaType,
            title: this.selectedFile!.name,
            description: '',
            position: this.lessonMediaItems.length,
            lessonId: 0
          };
          this.lessonMediaItems.push(mediaItem);
          this.selectedFile = null;
          this.uploadingFile = false;
          this.uploadProgress = 0;
        }
      },
      error: (error) => {
        console.error('Upload error:', error);
        this.uploadingFile = false;
        alert('Failed to upload file');
      }
    });
  }
  
  saveMediaItem(url: string): void {
    if (!this.selectedChapterId) return;
    
    const mediaItem: LessonMedia = {
      url: url,
      mediaType: this.mediaForm.get('mediaType')?.value,
      title: this.mediaForm.get('title')?.value || '',
      description: this.mediaForm.get('description')?.value || '',
      position: this.lessonMediaItems.length,
      lessonId: this.selectedChapterId
    };
    
    this.lessonMediaService.createMedia(mediaItem).subscribe({
      next: (created) => {
        this.lessonMediaItems.push(created);
        this.mediaForm.reset({ mediaType: LessonType.VIDEO });
        this.selectedFile = null;
        this.uploadingFile = false;
        this.uploadProgress = 0;
      },
      error: (error) => {
        console.error('Error saving media:', error);
        this.uploadingFile = false;
        alert('Failed to save media item');
      }
    });
  }
  
  deleteMediaItem(mediaId: number): void {
    if (mediaId) {
      this.lessonMediaService.deleteMedia(mediaId).subscribe({
        next: () => {
          this.lessonMediaItems = this.lessonMediaItems.filter(m => m.id !== mediaId);
        },
        error: (error) => console.error('Error deleting media:', error)
      });
    } else {
      this.lessonMediaItems = this.lessonMediaItems.filter(m => m.id !== mediaId);
    }
  }
  
  dropMedia(event: CdkDragDrop<LessonMedia[]>): void {
    moveItemInArray(this.lessonMediaItems, event.previousIndex, event.currentIndex);
  }

  dropChapter(event: CdkDragDrop<Chapter[]>): void {
    moveItemInArray(this.chapters, event.previousIndex, event.currentIndex);
    
    // Update orderIndex for all chapters
    const chapterUpdates = this.chapters.map((chapter, index) => {
      const updatedChapter = { ...chapter, orderIndex: index + 1 };
      return this.chapterService.updateChapter(chapter.id!, updatedChapter);
    });
    
    // Execute all updates
    Promise.all(chapterUpdates.map(obs => obs.toPromise()))
      .catch(error => console.error('Error reordering chapters:', error));
  }

  dropLesson(event: CdkDragDrop<Lesson[]>, chapterId: number): void {
    const lessons = this.getChapterLessons(chapterId);
    moveItemInArray(lessons, event.previousIndex, event.currentIndex);
    
    // Update orderIndex for all lessons in this chapter
    const lessonUpdates = lessons.map((lesson, index) => {
      const updatedLesson = { ...lesson, orderIndex: index + 1 };
      return this.lessonService.updateLesson(lesson.id!, updatedLesson);
    });
    
    // Execute all updates
    Promise.all(lessonUpdates.map(obs => obs.toPromise()))
      .then(() => {
        this.lessonsByChapter.set(chapterId, lessons);
      })
      .catch(error => console.error('Error reordering lessons:', error));
  }
  
  getMediaIcon(type: LessonType): string {
    switch (type) {
      case LessonType.VIDEO: return 'bi-play-circle';
      case LessonType.DOCUMENT: return 'bi-file-earmark';
      case LessonType.TEXT: return 'bi-file-text';
      case LessonType.QUIZ: return 'bi-question-circle';
      case LessonType.ASSIGNMENT: return 'bi-clipboard-check';
      case LessonType.INTERACTIVE: return 'bi-collection';
      default: return 'bi-file';
    }
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

    if (media.id) {
      this.lessonMediaService.updateMedia(media.id, updatedMedia).subscribe({
        next: () => {
          media.title = this.editingMediaTitle.trim();
          this.editingMediaId = null;
          this.editingMediaTitle = '';
        },
        error: (err) => console.error('Error updating media title:', err)
      });
    } else {
      // For unsaved media items (not yet in database)
      media.title = this.editingMediaTitle.trim();
      this.editingMediaId = null;
      this.editingMediaTitle = '';
    }
  }

  isEditingMedia(id: number | undefined): boolean {
    return this.editingMediaId === id;
  }
}
