import { Component, OnInit } from '@angular/core';
import { CommonModule, KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { EditorModule } from '@tinymce/tinymce-angular';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { CourseService } from '../../../core/services/course.service';
import { QuizService } from '../../../core/services/quiz.service';
import { AuthService } from '../../../core/services/auth.service';
import { OnlineLessonService, TutorAvailableSlots, AvailableTimeSlot } from '../../../core/services/online-lesson.service';
import { Lesson, LessonType, CreateLessonRequest, UpdateLessonRequest } from '../../../core/models/lesson.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Course } from '../../../core/models/course.model';
import * as mammoth from 'mammoth';

@Component({
  selector: 'app-lesson-management',
  standalone: true,
  imports: [CommonModule, FormsModule, EditorModule, KeyValuePipe],
  templateUrl: './lesson-management.component.html',
  styleUrl: './lesson-management.component.scss'
})
export class LessonManagementComponent implements OnInit {
  courseId!: number;
  chapterId!: number;
  course: Course | null = null;
  chapter: Chapter | null = null;
  lessons: Lesson[] = [];
  loading = false;
  availableQuizzes: any[] = [];
  
  // Modal states
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  showPreviewModal = false;
  
  // Lesson types
  lessonTypes = Object.values(LessonType);
  LessonType = LessonType;
  
  // TinyMCE configuration with free plugins only
  tinyMceConfig = {
    plugins: [
      'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 
      'image', 'link', 'lists', 'media', 'searchreplace', 
      'table', 'visualblocks', 'wordcount', 'code',
      'fullscreen', 'help', 'insertdatetime', 'preview'
    ],
    toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table | align | numlist bullist indent outdent | emoticons charmap | searchreplace | code fullscreen preview | help',
    height: 400,
    menubar: false,
    branding: false,
    resize: false
  };
  
  // Form data
  lessonForm: CreateLessonRequest = {
    title: '',
    description: '',
    content: '',
    contentUrl: '',
    lessonType: LessonType.TEXT,
    orderIndex: 0,
    duration: 0,
    isPreview: false,
    isPublished: false,
    chapterId: 0
  };
  
  selectedLesson: Lesson | null = null;
  selectedFile: File | null = null;
  uploadProgress = 0;
  uploadingFile = false;
  previewVideoUrl: any = null;
  filePreviewUrl: any = null;
  previewQuiz: any = null;
  currentQuestionPage = 0;
  questionsPerPage = 3;
  Math = Math;

  // Document conversion properties
  convertingDocument = false;
  conversionError: string | null = null;
  convertedFileName: string | null = null;
  showEditor = false;
  
  // Online lesson time slot properties
  tutorId: number = 0; // Will be set from logged-in user
  availableSlots: TutorAvailableSlots | null = null;
  selectedTimeSlot: AvailableTimeSlot | null = null;
  loadingSlots = false;
  noAvailabilityConfigured = false;
  currentTimeAssignment: any = null;
  loadingTimeAssignment = false;
  
  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly lessonService: LessonService,
    private readonly chapterService: ChapterService,
    private readonly courseService: CourseService,
    private readonly quizService: QuizService,
    private readonly sanitizer: DomSanitizer,
    private readonly onlineLessonService: OnlineLessonService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.chapterId = Number(this.route.snapshot.paramMap.get('chapterId'));
    
    this.lessonForm.chapterId = this.chapterId;
    
    // Get logged-in tutor ID
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.tutorId = currentUser.id;
    }
    
    this.loadCourse();
    this.loadChapter();
    this.loadLessons();
    this.loadQuizzes(); // Load quizzes for this course
  }

  loadQuizzes(): void {
    this.quizService.getQuizzesByCourse(this.courseId).subscribe({
      next: (quizzes) => {
        this.availableQuizzes = quizzes;
        console.log('Loaded quizzes for course:', quizzes);
      },
      error: (error) => {
        console.error('Error loading quizzes:', error);
        this.availableQuizzes = [];
      }
    });
  }

  loadCourse(): void {
    this.courseService.getCourseById(this.courseId).subscribe({
      next: (course) => {
        this.course = course;
      },
      error: (error) => {
        // Error loading course
      }
    });
  }

  loadChapter(): void {
    this.chapterService.getChapterById(this.chapterId).subscribe({
      next: (chapter) => {
        this.chapter = chapter;
      },
      error: (error) => {
        // Error loading chapter
      }
    });
  }

  loadLessons(): void {
    this.loading = true;
    this.lessonService.getLessonsByChapter(this.chapterId).subscribe({
      next: (lessons) => {
        this.lessons = lessons.sort((a, b) => a.orderIndex - b.orderIndex);
        this.loading = false;
      },
      error: (error) => {
        alert('Failed to load lessons: ' + (error.error?.error || error.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }

  openCreateModal(): void {
    this.lessonForm = {
      title: '',
      description: '',
      content: '',
      contentUrl: '',
      lessonType: LessonType.TEXT,
      orderIndex: this.lessons.length,
      duration: 0,
      isPreview: false,
      isPublished: false,
      chapterId: this.chapterId
    };
    this.selectedFile = null;
    this.selectedTimeSlot = null;
    this.availableSlots = null;
    this.noAvailabilityConfigured = false;
    this.showEditor = false;
    this.convertedFileName = null;
    this.conversionError = null;
    this.showCreateModal = true;
  }

  onLessonTypeChange(): void {
    if (this.lessonForm.lessonType === LessonType.ONLINE) {
      this.loadAvailableTimeSlots();
    } else {
      this.availableSlots = null;
      this.selectedTimeSlot = null;
      this.noAvailabilityConfigured = false;
    }
  }

  loadAvailableTimeSlots(): void {
    this.loadingSlots = true;
    this.noAvailabilityConfigured = false;
    
    this.onlineLessonService.getAvailableSlots(this.tutorId).subscribe({
      next: (slots) => {
        this.availableSlots = slots;
        this.loadingSlots = false;
        
        if (!slots.hasAvailability) {
          this.noAvailabilityConfigured = true;
        }
      },
      error: (error) => {
        this.loadingSlots = false;
        this.noAvailabilityConfigured = true;
      }
    });
  }

  selectTimeSlot(slot: AvailableTimeSlot): void {
    if (!slot.booked) {
      this.selectedTimeSlot = slot;
    }
  }

  getAvailableSlotsByDay(): Map<string, AvailableTimeSlot[]> {
    const slotsByDay = new Map<string, AvailableTimeSlot[]>();
    
    if (this.availableSlots) {
      this.availableSlots.availableSlots.forEach(slot => {
        if (!slotsByDay.has(slot.dayOfWeek)) {
          slotsByDay.set(slot.dayOfWeek, []);
        }
        slotsByDay.get(slot.dayOfWeek)!.push(slot);
      });
    }
    
    return slotsByDay;
  }

  getDayName(day: string): string {
    const days: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return days[day] || day;
  }

  getAvailableCountForDay(slots: AvailableTimeSlot[]): number {
    return slots.filter(slot => !slot.booked).length;
  }

  getTimeSlotClasses(slot: AvailableTimeSlot): string {
    const baseClasses = 'relative px-3 py-3 rounded-lg text-xs font-medium transition-all duration-200';
    
    if (slot.booked) {
      return `${baseClasses} bg-red-50 dark:bg-red-900/20 border-2 border-red-300 dark:border-red-700 text-red-400 dark:text-red-500 cursor-not-allowed`;
    }
    
    if (this.selectedTimeSlot === slot) {
      return `${baseClasses} bg-gradient-to-br from-blue-600 to-cyan-600 text-white border-2 border-blue-700 shadow-lg transform scale-105 font-bold`;
    }
    
    return `${baseClasses} bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border-2 border-gray-300 dark:border-gray-600 hover:border-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 hover:shadow-md cursor-pointer`;
  }

  openEditModal(lesson: Lesson): void {
    this.selectedLesson = lesson;
    this.lessonForm = {
      title: lesson.title,
      description: lesson.description,
      content: lesson.content || '',
      contentUrl: lesson.contentUrl || '',
      lessonType: lesson.lessonType,
      orderIndex: lesson.orderIndex,
      duration: lesson.duration || 0,
      isPreview: lesson.isPreview,
      isPublished: lesson.isPublished,
      chapterId: lesson.chapterId,
      quizId: lesson.quizId
    };
    this.selectedFile = null;
    
    // For DOCUMENT type lessons, if there's HTML content, show the editor
    if (lesson.lessonType === LessonType.DOCUMENT && lesson.content && lesson.content.trim().length > 0) {
      this.showEditor = true;
      this.convertedFileName = lesson.title || 'document';
    } else {
      this.showEditor = false;
      this.convertedFileName = null;
    }
    
    this.showEditModal = true;
  }

  openDeleteModal(lesson: Lesson): void {
    this.selectedLesson = lesson;
    this.showDeleteModal = true;
  }

  openPreviewModal(lesson: Lesson): void {
    this.selectedLesson = lesson;
    this.previewVideoUrl = null;
    this.currentTimeAssignment = null;
    this.previewQuiz = null;
    this.currentQuestionPage = 0;
    
    // Process video URL if it's a video lesson
    if (lesson.lessonType === LessonType.VIDEO && lesson.contentUrl) {
      if (lesson.contentUrl.includes('youtube') || lesson.contentUrl.includes('youtu.be') || lesson.contentUrl.includes('vimeo')) {
        this.previewVideoUrl = this.getEmbedUrl(lesson.contentUrl);
      }
    }

    // Load quiz data for QUIZ lessons
    if (lesson.lessonType === LessonType.QUIZ && lesson.quizId) {
      this.quizService.getQuizById(lesson.quizId).subscribe({
        next: (quiz) => {
          this.previewQuiz = quiz;
          // Load questions separately
          this.quizService.getQuestionsByQuizId(lesson.quizId!).subscribe({
            next: (questions) => {
              this.previewQuiz.questions = questions;
            },
            error: (error) => {
              console.error('Error loading questions:', error);
            }
          });
        },
        error: (error) => {
          console.error('Error loading quiz:', error);
          this.previewQuiz = null;
        }
      });
    }

    // Load time assignment for ONLINE lessons
    if (lesson.lessonType === LessonType.ONLINE && lesson.id) {
      this.loadingTimeAssignment = true;
      this.onlineLessonService.getTimeAssignment(lesson.id).subscribe({
        next: (assignment) => {
          this.currentTimeAssignment = assignment;
          this.loadingTimeAssignment = false;
        },
        error: () => {
          this.currentTimeAssignment = null;
          this.loadingTimeAssignment = false;
        }
      });
    }
    
    this.showPreviewModal = true;
  }

  getEmbedUrl(url: string): SafeResourceUrl {
    let embedUrl = url;
    
    // Convert YouTube URLs to embed format
    if (url.includes('youtube.com/watch')) {
      const videoId = url.split('v=')[1]?.split('&')[0];
      embedUrl = `https://www.youtube.com/embed/${videoId}`;
    } else if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1]?.split('?')[0];
      embedUrl = `https://www.youtube.com/embed/${videoId}`;
    }
    // Convert Vimeo URLs to embed format
    else if (url.includes('vimeo.com/')) {
      const videoId = url.split('vimeo.com/')[1]?.split('?')[0];
      embedUrl = `https://player.vimeo.com/video/${videoId}`;
    }
    
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }

  getDocumentPreviewUrl(contentUrl: string): SafeResourceUrl {
    const cleanUrl = contentUrl.startsWith('/') ? contentUrl.substring(1) : contentUrl;
    const url = `http://localhost:8086/${cleanUrl}`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  getDocumentDownloadUrl(contentUrl: string): string {
    const cleanUrl = contentUrl.startsWith('/') ? contentUrl.substring(1) : contentUrl;
    return `http://localhost:8086/${cleanUrl}`;
  }

  closeModals(): void {
    this.showCreateModal = false;
    this.showEditModal = false;
    this.showDeleteModal = false;
    this.showPreviewModal = false;
    this.selectedLesson = null;
    this.selectedFile = null;
    this.previewQuiz = null;
    this.previewVideoUrl = null;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      
      // Handle document conversion for DOCUMENT type lessons
      if (this.lessonForm.lessonType === LessonType.DOCUMENT) {
        const fileExtension = file.name.split('.').pop()?.toLowerCase();
        
        if (fileExtension === 'pdf' || fileExtension === 'docx') {
          this.convertDocumentToHtml(file);
          return;
        } else {
          this.conversionError = 'Only PDF and DOCX files can be converted to editable content. Other file types will be uploaded as downloadable documents.';
        }
      }
      
      // Create preview URL for the file
      if (file.type.startsWith('video/')) {
        this.filePreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(file));
      } else if (file.type === 'application/pdf') {
        this.filePreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(file));
      } else {
        this.filePreviewUrl = null;
      }
    }
  }

  removeFile(): void {
    this.selectedFile = null;
    this.uploadProgress = 0;
    this.filePreviewUrl = null;
  }

  getAcceptedFileTypes(): string {
    switch (this.lessonForm.lessonType) {
      case LessonType.VIDEO:
        return 'video/*';
      case LessonType.DOCUMENT:
        return '.docx';
      default:
        return '*';
    }
  }

  getFileIcon(): string {
    if (!this.selectedFile) return '';
    
    const type = this.selectedFile.type;
    if (type.startsWith('video/')) return '­ƒÄÑ';
    if (type.startsWith('image/')) return '­ƒû╝´©Å';
    if (type.includes('pdf')) return '­ƒôä';
    if (type.includes('word') || type.includes('document')) return '­ƒôØ';
    if (type.includes('sheet') || type.includes('excel')) return '­ƒôè';
    if (type.includes('presentation') || type.includes('powerpoint')) return '­ƒôè';
    return '­ƒôü';
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  async convertDocumentToHtml(file: File): Promise<void> {
    this.convertingDocument = true;
    this.conversionError = null;
    this.convertedFileName = file.name;

    try {
      const fileExtension = file.name.split('.').pop()?.toLowerCase();

      if (fileExtension === 'docx') {
        await this.convertWordToHtml(file);
        this.showEditor = true;
      } else {
        this.conversionError = 'Only DOCX files are supported. Please upload a .docx file.';
      }
    } catch (error) {
      console.error('Document conversion error:', error);
      this.conversionError = 'Failed to convert document. Please try again or upload a different file.';
    } finally {
      this.convertingDocument = false;
    }
  }

  private async convertWordToHtml(file: File): Promise<void> {
    const arrayBuffer = await file.arrayBuffer();
    const result = await mammoth.convertToHtml({ arrayBuffer });
    this.lessonForm.content = result.value;
    // Clear contentUrl since we're using HTML content instead
    this.lessonForm.contentUrl = '';
  }

  changeFile(): void {
    this.showEditor = false;
    this.convertedFileName = null;
    this.lessonForm.content = '';
    this.selectedFile = null;
    this.conversionError = null;
  }

  // FIX 3: Publish validation method
  canPublish(lesson: any): boolean {
    switch (lesson.lessonType) {
      case LessonType.VIDEO:
        return !!(lesson.contentUrl && lesson.contentUrl.trim().length > 0);
      case LessonType.DOCUMENT:
        return !!(
          (lesson.contentUrl && lesson.contentUrl.trim().length > 0) ||
          (lesson.content && lesson.content.trim().length > 0)
        );
      case LessonType.TEXT:
        return !!(lesson.content && lesson.content.trim().length > 0);
      case LessonType.QUIZ:
        return !!(lesson.quizId); // Can publish if quiz is selected
      case LessonType.ASSIGNMENT:
        return !!(
          (lesson.content && lesson.content.trim().length > 0) ||
          (lesson.contentUrl && lesson.contentUrl.trim().length > 0)
        );
      case LessonType.INTERACTIVE:
        return !!(lesson.contentUrl && lesson.contentUrl.trim().length > 0);
      case LessonType.ONLINE:
        return true; // Online lessons can be published without additional content
      default:
        return false;
    }
  }

  getPublishTooltip(lesson: any): string {
    if (lesson.lessonType === LessonType.QUIZ) {
      return lesson.quizId ? 'Publish this quiz lesson' : 'Select a quiz before publishing';
    }
    return 'Add content before publishing';
  }

  // Helper methods for template null checking
  hasDocumentContent(lesson: any): boolean {
    return lesson?.content && lesson.content.trim().length > 0;
  }

  hasNoDocumentContent(lesson: any): boolean {
    return !lesson?.content || lesson.content.trim().length === 0;
  }

  getDocumentContent(lesson: any): string {
    return lesson?.content || '';
  }

  createLesson(): void {
    if (!this.lessonForm.title || !this.lessonForm.description) {
      alert('Please fill in all required fields');
      return;
    }

    // Validate time slot for ONLINE lessons
    if (this.lessonForm.lessonType === LessonType.ONLINE && !this.selectedTimeSlot) {
      alert('Please select a time slot for the online lesson');
      return;
    }

    this.loading = true;
    
    // For DOCUMENT type lessons that have been converted to HTML, don't upload the file
    const shouldUploadFile = !!(this.selectedFile && 
      !(this.lessonForm.lessonType === LessonType.DOCUMENT && this.showEditor && this.lessonForm.content));
    
    // First create the lesson
    this.lessonService.createLesson(this.lessonForm).subscribe({
      next: (createdLesson) => {
        // If it's an ONLINE lesson, assign the time slot
        if (this.lessonForm.lessonType === LessonType.ONLINE && this.selectedTimeSlot && createdLesson.id) {
          this.onlineLessonService.assignTimeSlot(createdLesson.id, this.tutorId, {
            dayOfWeek: this.selectedTimeSlot.dayOfWeek,
            startTime: this.selectedTimeSlot.startTime,
            endTime: this.selectedTimeSlot.endTime
          }).subscribe({
            next: () => {
              if (createdLesson.id) {
                this.finishLessonCreation(createdLesson.id, shouldUploadFile);
              }
            },
            error: (error) => {
              const errorMsg = error.error || 'Unknown error';
              if (errorMsg.includes('already booked')) {
                alert('This time slot is no longer available. It may have been booked by another lesson. Please select a different time slot.');
                // Reload available slots to refresh the list
                this.loadAvailableTimeSlots();
              } else {
                alert('Lesson created but failed to assign time slot: ' + errorMsg);
              }
              if (createdLesson.id) {
                this.finishLessonCreation(createdLesson.id, shouldUploadFile);
              }
            }
          });
        } else if (createdLesson.id) {
          // Not an ONLINE lesson or no time slot, proceed normally
          this.finishLessonCreation(createdLesson.id, shouldUploadFile);
        }
      },
      error: (error) => {
        alert('Error creating lesson: ' + (error.error?.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }

  private finishLessonCreation(lessonId: number | undefined, shouldUploadFile: boolean): void {
    if (!lessonId) {
      this.loading = false;
      return;
    }
    
    // If there's a file to upload and it's not a converted document, upload it
    if (shouldUploadFile) {
      this.uploadingFile = true;
      const uploadObservable = this.lessonForm.lessonType === LessonType.VIDEO
        ? this.lessonService.uploadVideo(lessonId, this.selectedFile!)
        : this.lessonService.uploadDocument(lessonId, this.selectedFile!);
      
      uploadObservable.subscribe({
        next: (response) => {
          this.uploadingFile = false;
          this.uploadProgress = 100;
          this.loadLessons();
          this.closeModals();
          this.loading = false;
        },
        error: (error) => {
          this.uploadingFile = false;
          alert('Lesson created but file upload failed: ' + (error.error?.error || 'Unknown error'));
          this.loadLessons();
          this.closeModals();
          this.loading = false;
        }
      });
    } else {
      // No file to upload, just reload
      this.loadLessons();
      this.closeModals();
      this.loading = false;
    }
  }

  updateLesson(): void {
      if (!this.selectedLesson?.id) return;

      if (!this.lessonForm.title || !this.lessonForm.description) {
        alert('Please fill in all required fields');
        return;
      }

      this.loading = true;
      const updateRequest: UpdateLessonRequest = {
        title: this.lessonForm.title,
        description: this.lessonForm.description,
        content: this.lessonForm.content,
        contentUrl: this.lessonForm.contentUrl,
        lessonType: this.lessonForm.lessonType,
        orderIndex: this.lessonForm.orderIndex,
        duration: this.lessonForm.duration,
        isPreview: this.lessonForm.isPreview,
        isPublished: this.lessonForm.isPublished,
        chapterId: this.chapterId
      };

      // FIX 1: Check if we need to replace existing file
      const hasExistingFile = this.selectedLesson.contentUrl && this.selectedLesson.contentUrl.trim().length > 0;
      const hasNewFile = this.selectedFile !== null;

      if (hasExistingFile && hasNewFile) {
        // Confirm file replacement
        const confirmed = confirm('This will replace the existing file. Continue?');
        if (!confirmed) {
          // User cancelled - clear selected file and keep existing
          this.selectedFile = null;
          this.loading = false;
          return;
        }

        // Delete old file first
        this.lessonService.deleteContentFile(this.selectedLesson.id).subscribe({
          next: () => {
            // Now proceed with update and new file upload
            this.proceedWithUpdate(updateRequest);
          },
          error: (error) => {
            alert('Failed to delete old file. Please try again.');
            this.loading = false;
          }
        });
      } else {
        // No file replacement needed, proceed normally
        this.proceedWithUpdate(updateRequest);
      }
    }

    private proceedWithUpdate(updateRequest: UpdateLessonRequest): void {
      if (!this.selectedLesson?.id) return;

      // For DOCUMENT type lessons that have been converted to HTML, don't upload the file
      const shouldUploadFile = this.selectedFile && 
        !(this.lessonForm.lessonType === LessonType.DOCUMENT && this.showEditor && this.lessonForm.content);

      // First update the lesson
      this.lessonService.updateLesson(this.selectedLesson.id, updateRequest).subscribe({
        next: (updatedLesson) => {
          // If there's a new file to upload and it's not a converted document, upload it
          if (shouldUploadFile && updatedLesson.id) {
            this.uploadingFile = true;
            const uploadObservable = this.lessonForm.lessonType === LessonType.VIDEO
              ? this.lessonService.uploadVideo(updatedLesson.id, this.selectedFile!)
              : this.lessonService.uploadDocument(updatedLesson.id, this.selectedFile!);

            uploadObservable.subscribe({
              next: (response) => {
                this.uploadingFile = false;
                this.uploadProgress = 100;
                this.loadLessons();
                this.closeModals();
                this.loading = false;
              },
              error: (error) => {
                this.uploadingFile = false;
                alert('Lesson updated but file upload failed: ' + (error.error?.error || 'Unknown error'));
                this.loadLessons();
                this.closeModals();
                this.loading = false;
              }
            });
          } else {
            // No file to upload, just reload
            this.loadLessons();
            this.closeModals();
            this.loading = false;
          }
        },
        error: (error) => {
          alert('Error updating lesson: ' + (error.error?.message || 'Unknown error'));
          this.loading = false;
        }
      });
    }


  deleteLesson(): void {
    if (!this.selectedLesson?.id) return;
    
    this.loading = true;
    this.lessonService.deleteLesson(this.selectedLesson.id).subscribe({
      next: () => {
        this.loadLessons();
        this.closeModals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error deleting lesson:', error);
        this.loading = false;
      }
    });
  }

  togglePublish(lesson: Lesson): void {
    if (!lesson.id) return;
    
    const updateRequest: UpdateLessonRequest = {
      title: lesson.title,
      description: lesson.description,
      content: lesson.content,
      contentUrl: lesson.contentUrl,
      lessonType: lesson.lessonType,
      orderIndex: lesson.orderIndex,
      duration: lesson.duration,
      isPreview: lesson.isPreview,
      isPublished: !lesson.isPublished,
      chapterId: lesson.chapterId
    };
    
    this.lessonService.updateLesson(lesson.id, updateRequest).subscribe({
      next: () => {
        this.loadLessons();
      },
      error: (error) => {
        console.error('Error toggling publish status:', error);
      }
    });
  }

  // Online lesson start logic
  canStartLesson(assignment: any): boolean {
    if (!assignment) return false;
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[assignment.dayOfWeek];
    if (now.getDay() !== lessonDay) return false;

    const [startH, startM] = assignment.startTime.split(':').map(Number);
    const [endH, endM] = assignment.endTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    // Enable button 15 minutes before start time until end time
    return nowMinutes >= startMinutes - 15 && nowMinutes <= endMinutes;
  }

  getTimeUntilEarlyStart(assignment: any): string {
    if (!assignment) return '';
    
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[assignment.dayOfWeek];
    const currentDay = now.getDay();
    
    // Calculate days until lesson day
    let daysUntil = lessonDay - currentDay;
    if (daysUntil < 0) daysUntil += 7; // Next week
    
    const [startH, startM] = assignment.startTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const earlyStartMinutes = startMinutes - 15;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();
    
    // If it's the same day
    if (daysUntil === 0) {
      const minutesUntilEarlyStart = earlyStartMinutes - nowMinutes;
      
      if (minutesUntilEarlyStart <= 0) {
        // Already in or past early start window
        return 'Available now';
      }
      
      const hours = Math.floor(minutesUntilEarlyStart / 60);
      const minutes = minutesUntilEarlyStart % 60;
      
      if (hours > 0) {
        return `Available in ${hours}h ${minutes}m`;
      } else {
        return `Available in ${minutes}m`;
      }
    }
    
    // Different day
    if (daysUntil === 1) {
      return `Available tomorrow at ${assignment.startTime}`;
    } else {
      const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      return `Available on ${dayNames[lessonDay]} at ${assignment.startTime}`;
    }
  }

  startLesson(lesson: Lesson): void {
    if (!lesson.id) return;
    const roomId = `lesson-${lesson.id}-${Date.now()}`;
    this.router.navigate(['/meeting', roomId], {
      queryParams: { lessonId: lesson.id }
    });
  }

  goBack(): void {
    this.router.navigate(['/tutor-panel/courses', this.courseId, 'chapters']);
  }

  getTotalDuration(): number {
    return this.lessons.reduce((sum, lesson) => sum + (lesson.duration || 0), 0);
  }

  getPublishedCount(): number {
    return this.lessons.filter(lesson => lesson.isPublished).length;
  }

  getPreviewCount(): number {
    return this.lessons.filter(lesson => lesson.isPreview).length;
  }

  getLessonTypeIcon(type: LessonType): string {
    const icons = {
      [LessonType.VIDEO]: '🎥',
      [LessonType.TEXT]: '📄',
      [LessonType.DOCUMENT]: '📋',
      [LessonType.QUIZ]: '📝',
      [LessonType.ASSIGNMENT]: '📌',
      [LessonType.INTERACTIVE]: '🎮',
      [LessonType.ONLINE]: '🎦'
    };
    return icons[type] || '📚';
  }

  getLessonTypeColor(type: LessonType): string {
    const colors = {
      [LessonType.VIDEO]: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      [LessonType.TEXT]: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      [LessonType.DOCUMENT]: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      [LessonType.QUIZ]: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
      [LessonType.ASSIGNMENT]: 'bg-pink-100 text-pink-700 dark:bg-pink-900/30 dark:text-pink-300',
      [LessonType.INTERACTIVE]: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      [LessonType.ONLINE]: 'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-300'
    };
    return colors[type] || 'bg-gray-100 text-gray-700';
  }

  // Quiz pagination methods
  getPaginatedQuestions(): any[] {
    if (!this.previewQuiz?.questions) return [];
    const start = this.currentQuestionPage * this.questionsPerPage;
    const end = start + this.questionsPerPage;
    return this.previewQuiz.questions.slice(start, end);
  }

  getTotalQuestionPages(): number {
    if (!this.previewQuiz?.questions) return 0;
    return Math.ceil(this.previewQuiz.questions.length / this.questionsPerPage);
  }

  nextQuestionPage(): void {
    if (this.currentQuestionPage < this.getTotalQuestionPages() - 1) {
      this.currentQuestionPage++;
    }
  }

  previousQuestionPage(): void {
    if (this.currentQuestionPage > 0) {
      this.currentQuestionPage--;
    }
  }

  goToQuestionPage(page: number): void {
    this.currentQuestionPage = page;
  }

}
