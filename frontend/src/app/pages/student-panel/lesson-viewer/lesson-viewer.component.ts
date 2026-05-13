import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { LessonService } from '../../../core/services/lesson.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { CourseService } from '../../../core/services/course.service';
import { QuizService } from '../../../core/services/quiz.service';
import { LessonProgressService } from '../../../core/services/lesson-progress.service';
import { AuthService } from '../../../core/services/auth.service';
import { StudentAnalyticsService } from '../../../services/student-analytics.service';
import { OnlineLessonService, LessonTimeAssignment } from '../../../core/services/online-lesson.service';
import { Lesson } from '../../../core/models/lesson.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Course } from '../../../core/models/course.model';
import { Subscription } from 'rxjs';

interface ChapterWithLessons {
  chapter: Chapter;
  lessons: Lesson[];
  isExpanded: boolean;
}

@Component({
  selector: 'app-lesson-viewer',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './lesson-viewer.component.html',
  styleUrls: ['./lesson-viewer.component.scss']
})
export class LessonViewerComponent implements OnInit, OnDestroy {
  lessonId!: number;
  courseId!: number;
  lesson: Lesson | null = null;
  course: Course | null = null;
  chaptersWithLessons: ChapterWithLessons[] = [];
  
  loading = true;
  videoUrl: SafeResourceUrl | null = null;
  documentUrl: SafeResourceUrl | null = null;
  isCompleted = false;
  sidebarCollapsed = false;
  
  // Quiz properties
  currentQuiz: any = null;
  quizQuestions: any[] = [];
  loadingQuiz = false;
  quizStarted = false;
  currentQuestionIndex = 0;
  studentAnswers: { [questionId: number]: string } = {};
  quizSubmitted = false;
  quizResult: any = null;
  quizAttemptExists = false;
  
  // Timer properties
  totalTimeRemaining = 0; // Total quiz time in seconds
  questionTimeRemaining = 0; // Suggested time per question in seconds
  quizTimerInterval: any = null;
  timePerQuestion = 0; // Calculated time per question
  
  // Online lesson properties
  lessonTimeAssignment: LessonTimeAssignment | null = null;
  loadingTimeAssignment = false;
  activeMeetingRoomId: string | null = null;
  checkingActiveMeeting = false;
  private activeMeetingCheckInterval: any = null;
  
  private progressSubscription?: Subscription;
  private currentStudentId: number = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly lessonService: LessonService,
    private readonly chapterService: ChapterService,
    private readonly courseService: CourseService,
    private readonly quizService: QuizService,
    private readonly progressService: LessonProgressService,
    private readonly authService: AuthService,
    private readonly sanitizer: DomSanitizer,
    private readonly onlineLessonService: OnlineLessonService,
    private readonly analyticsService: StudentAnalyticsService
  ) {}

  ngOnInit(): void {
    this.lessonId = +this.route.snapshot.paramMap.get('id')!;
    
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.currentStudentId = currentUser.id;
      
      // 🎯 TRACKER L'OUVERTURE DE LA LEÇON
      if (currentUser.role === 'STUDENT') {
        this.analyticsService.trackLessonOpened(currentUser.id).subscribe({
          next: () => console.log('✅ Lesson opened tracked'),
          error: (err: any) => console.error('❌ Error tracking lesson opened:', err)
        });
      }
    }
    
    this.loadLesson();
    
    // Subscribe to progress updates
    this.progressSubscription = this.progressService.progressUpdate$.subscribe(lessonId => {
      if (lessonId) {
        // Refresh to show updated completion status
        this.updateCompletionStatus();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.progressSubscription) {
      this.progressSubscription.unsubscribe();
    }
    
    // Clear active meeting check interval
    if (this.activeMeetingCheckInterval) {
      clearInterval(this.activeMeetingCheckInterval);
    }
    
    // Clear quiz timer
    this.stopTimer();
  }

  loadLesson(): void {
    this.loading = true;
    this.lessonService.getLessonById(this.lessonId).subscribe({
      next: (lesson) => {
        this.lesson = lesson;
        
        // Load chapter to get courseId
        if (lesson.chapterId) {
          this.chapterService.getChapterById(lesson.chapterId).subscribe({
            next: (chapter) => {
              this.courseId = chapter.courseId;
              
              // Load full course structure
              this.loadCourseStructure(this.courseId);
              
              // Load progress for this course
              this.loadProgress();
            },
            error: (error) => {
              console.error('Error loading chapter:', error);
              this.loading = false;
            }
          });
        }
        
        // Process video URL if it's a video lesson
        if (lesson.lessonType === 'VIDEO') {
          if (lesson.contentUrl && lesson.contentUrl.trim() !== '') {
            // Check if it's a YouTube/Vimeo URL or local file
            if (lesson.contentUrl.includes('youtube') || lesson.contentUrl.includes('youtu.be') || lesson.contentUrl.includes('vimeo')) {
              this.videoUrl = this.getEmbedUrl(lesson.contentUrl);
            } else {
              // Local video file - sanitize the URL
              // Remove leading slash if present to avoid double slash
              const cleanUrl = lesson.contentUrl.startsWith('/') ? lesson.contentUrl.substring(1) : lesson.contentUrl;
              const localUrl = `http://localhost:8086/${cleanUrl}`;
              this.videoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(localUrl);
            }
          } else {
            this.videoUrl = null;
          }
        }
        
        // Process document URL if it's a document lesson
        // Only set documentUrl if there's a contentUrl AND no HTML content
        if (lesson.lessonType === 'DOCUMENT' && lesson.contentUrl && (!lesson.content || lesson.content.trim().length === 0)) {
          // Remove leading slash if present to avoid double slash
          const cleanUrl = lesson.contentUrl.startsWith('/') ? lesson.contentUrl.substring(1) : lesson.contentUrl;
          const docUrl = `http://localhost:8086/${cleanUrl}`;
          this.documentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(docUrl);
        } else {
          this.documentUrl = null;
        }
        
        // Load quiz for QUIZ lessons
        if (lesson.lessonType === 'QUIZ' && lesson.quizId) {
          this.loadQuiz(lesson.quizId);
        }
        
        // Load time assignment for ONLINE lessons
        if (lesson.lessonType === 'ONLINE' && lesson.id) {
          this.loadTimeAssignment(lesson.id);
        }
      },
      error: (error) => {
        console.error('Error loading lesson:', error);
        this.loading = false;
      }
    });
  }
  
  loadTimeAssignment(lessonId: number): void {
    this.loadingTimeAssignment = true;
    this.onlineLessonService.getTimeAssignment(lessonId).subscribe({
      next: (assignment) => {
        this.lessonTimeAssignment = assignment;
        this.loadingTimeAssignment = false;
        
        // Start checking for active meeting every 10 seconds
        this.startActiveMeetingCheck(lessonId);
      },
      error: () => {
        this.lessonTimeAssignment = null;
        this.loadingTimeAssignment = false;
      }
    });
  }

  startActiveMeetingCheck(lessonId: number): void {
    // Clear any existing interval
    if (this.activeMeetingCheckInterval) {
      clearInterval(this.activeMeetingCheckInterval);
    }
    
    // Check immediately
    this.checkActiveMeeting(lessonId);
    
    // Then check every 10 seconds
    this.activeMeetingCheckInterval = setInterval(() => {
      this.checkActiveMeeting(lessonId);
    }, 10000);
  }

  checkActiveMeeting(lessonId: number): void {
    this.checkingActiveMeeting = true;
    this.onlineLessonService.checkActiveMeeting(lessonId).subscribe({
      next: (response) => {
        this.activeMeetingRoomId = response.active ? response.roomId : null;
        this.checkingActiveMeeting = false;
      },
      error: () => {
        this.activeMeetingRoomId = null;
        this.checkingActiveMeeting = false;
      }
    });
  }

  loadCourseStructure(courseId: number): void {
    // Load course info
    this.courseService.getCourseById(courseId).subscribe({
      next: (course) => {
        this.course = course;
      },
      error: (error) => {
        console.error('Error loading course:', error);
      }
    });

    // Load all chapters with their lessons
    this.chapterService.getChaptersByCourse(courseId).subscribe({
      next: (chapters) => {
        const sortedChapters = chapters
          .filter(c => c.isPublished)
          .sort((a, b) => a.orderIndex - b.orderIndex);
        
        // Load lessons for each chapter
        const chapterPromises = sortedChapters.map(chapter => {
          return new Promise<ChapterWithLessons>((resolve) => {
            this.lessonService.getLessonsByChapter(chapter.id!).subscribe({
              next: (lessons) => {
                const sortedLessons = lessons
                  .filter(l => l.isPublished)
                  .sort((a, b) => a.orderIndex - b.orderIndex);
                
                // Expand chapter if it contains current lesson
                const isExpanded = sortedLessons.some(l => l.id === this.lessonId);
                
                resolve({
                  chapter,
                  lessons: sortedLessons,
                  isExpanded
                });
              },
              error: () => {
                resolve({
                  chapter,
                  lessons: [],
                  isExpanded: false
                });
              }
            });
          });
        });

        Promise.all(chapterPromises).then(chaptersWithLessons => {
          this.chaptersWithLessons = chaptersWithLessons;
          this.loading = false;
        });
      },
      error: (error) => {
        console.error('Error loading chapters:', error);
        this.loading = false;
      }
    });
  }

  loadProgress(): void {
    if (!this.currentStudentId || !this.courseId) return;
    
    this.progressService.getProgressByStudentAndCourse(this.currentStudentId, this.courseId).subscribe({
      next: (progressList) => {
        // Check if current lesson is completed
        const currentProgress = progressList.find(p => p.lessonId === this.lessonId);
        this.isCompleted = currentProgress?.isCompleted || false;
      },
      error: (error) => {
        console.error('Error loading progress:', error);
      }
    });
  }

  updateCompletionStatus(): void {
    // Refresh completion status without reloading everything
    if (!this.currentStudentId || !this.courseId) return;
    
    this.progressService.getProgressByStudentAndCourse(this.currentStudentId, this.courseId).subscribe({
      next: () => {
        // Cache is updated automatically in the service
      }
    });
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

  markAsComplete(): void {
    if (!this.lesson || !this.currentStudentId || !this.courseId) return;
    
    this.progressService.markLessonComplete(
      this.currentStudentId,
      this.lessonId,
      this.courseId
    ).subscribe({
      next: () => {
        this.isCompleted = true;
        
        // Auto-navigate to next lesson after 1 second
        setTimeout(() => {
          this.goToNextLesson();
        }, 1000);
      },
      error: (error) => {
        console.error('Error marking lesson complete:', error);
        alert('Failed to mark lesson as complete. Please try again.');
      }
    });
  }

  goToPreviousLesson(): void {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return;
    
    const currentIndex = allLessons.findIndex(l => l.id === this.lessonId);
    if (currentIndex > 0) {
      const previousLesson = allLessons[currentIndex - 1];
      this.navigateToLesson(previousLesson);
    }
  }

  goToNextLesson(): void {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return;
    
    const currentIndex = allLessons.findIndex(l => l.id === this.lessonId);
    if (currentIndex < allLessons.length - 1) {
      const nextLesson = allLessons[currentIndex + 1];
      
      // Check if next lesson is unlocked
      if (this.isLessonUnlocked(nextLesson)) {
        this.navigateToLesson(nextLesson);
      }
      // Removed alert - just don't navigate if locked
    } else {
      // Last lesson - go back to course learning
      // Removed congratulations alert
      this.goBack();
    }
  }

  hasPreviousLesson(): boolean {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return false;
    const currentIndex = allLessons.findIndex(l => l.id === this.lessonId);
    return currentIndex > 0;
  }

  hasNextLesson(): boolean {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return false;
    const currentIndex = allLessons.findIndex(l => l.id === this.lessonId);
    return currentIndex < allLessons.length - 1;
  }

  getAllLessonsFlat(): Lesson[] {
    const allLessons: Lesson[] = [];
    this.chaptersWithLessons.forEach(cwl => {
      allLessons.push(...cwl.lessons);
    });
    return allLessons;
  }

  downloadDocument(): void {
    if (this.lesson?.contentUrl) {
      // Remove leading slash if present to avoid double slash
      const cleanUrl = this.lesson.contentUrl.startsWith('/') ? this.lesson.contentUrl.substring(1) : this.lesson.contentUrl;
      const url = `http://localhost:8086/${cleanUrl}`;
      window.open(url, '_blank');
    }
  }

  goBack(): void {
    // Navigate back to course learning page
    if (this.courseId) {
      this.router.navigate(['/user-panel/course', this.courseId, 'learning']);
    } else {
      this.router.navigate(['/user-panel/my-packs']);
    }
  }

  toggleChapter(chapterWithLessons: ChapterWithLessons): void {
    chapterWithLessons.isExpanded = !chapterWithLessons.isExpanded;
  }

  navigateToLesson(lesson: Lesson): void {
    if (lesson.id === this.lessonId) return;
    
    // Check if lesson is unlocked
    if (!this.isLessonUnlocked(lesson)) {
      // Removed alert - just don't navigate if locked
      return;
    }
    
    this.router.navigate(['/user-panel/lesson', lesson.id]);
    this.lessonId = lesson.id!;
    this.loadLesson();
  }

  isLessonUnlocked(lesson: Lesson): boolean {
    if (!lesson.id) return false;
    
    // First lesson is always unlocked
    const allLessons = this.getAllLessonsFlat();
    const lessonIndex = allLessons.findIndex(l => l.id === lesson.id);
    
    if (lessonIndex === 0) return true;
    
    // Check if previous lesson is completed
    const previousLesson = allLessons[lessonIndex - 1];
    return this.isLessonCompleted(previousLesson.id!);
  }

  isLessonCompleted(lessonId: number): boolean {
    return this.progressService.isLessonCompleted(this.courseId, lessonId);
  }

  isLessonActive(lessonId: number): boolean {
    return lessonId === this.lessonId;
  }

  getLessonProgress(): number {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return 0;
    
    const completedCount = allLessons.filter(l => this.isLessonCompleted(l.id!)).length;
    return (completedCount / allLessons.length) * 100;
  }

  getCurrentLessonPosition(): string {
    const allLessons = this.getAllLessonsFlat();
    if (allLessons.length === 0) return '0/0';
    
    const currentIndex = allLessons.findIndex(l => l.id === this.lessonId);
    return `${currentIndex + 1}/${allLessons.length}`;
  }

  getLessonIcon(lessonType?: string): string {
    if (!lessonType && !this.lesson) return '📚';
    const type = lessonType || this.lesson?.lessonType;
    
    switch (type) {
      case 'VIDEO': return '🎥';
      case 'TEXT': return '📄';
      case 'QUIZ': return '📝';
      case 'ASSIGNMENT': return '📌';
      case 'DOCUMENT': return '📋';
      case 'INTERACTIVE': return '🎮';
      case 'ONLINE': return '🎦';
      default: return '📚';
    }
  }

  formatDuration(minutes: number): string {
    if (minutes < 60) {
      return `${minutes}min`;
    }
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}h ${mins}min` : `${hours}h`;
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  getLessonTypeColor(lessonType: string): string {
    switch (lessonType) {
      case 'VIDEO': return '#ef4444';
      case 'TEXT': return '#3b82f6';
      case 'QUIZ': return '#f59e0b';
      case 'ASSIGNMENT': return '#8b5cf6';
      case 'DOCUMENT': return '#10b981';
      case 'INTERACTIVE': return '#ec4899';
      default: return '#6b7280';
    }
  }

  // Helper methods for DOCUMENT lessons with HTML content
  hasDocumentHtmlContent(): boolean {
    return !!(this.lesson?.lessonType === 'DOCUMENT' && this.lesson?.content && this.lesson.content.trim().length > 0);
  }

  getSafeDocumentHtml() {
    if (!this.lesson?.content) return '';
    return this.sanitizer.bypassSecurityTrustHtml(this.lesson.content);
  }

  getSafeTextHtml() {
    if (!this.lesson?.content) return '';
    return this.sanitizer.bypassSecurityTrustHtml(this.lesson.content);
  }

  onQuizCompleted(): void {
    // Mark lesson as complete when quiz is completed
    this.markAsComplete();
  }

  // Online lesson helper methods
  getDayName(dayOfWeek: string): string {
    const days: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return days[dayOfWeek] || dayOfWeek;
  }

  canJoinLesson(): boolean {
    if (!this.lesson?.lessonType || this.lesson.lessonType !== 'ONLINE') return false;
    if (!this.courseId || !this.currentStudentId) return false;
    
    // If tutor has started the meeting, allow join regardless of schedule
    if (this.activeMeetingRoomId) return true;
    
    // Otherwise, check schedule
    if (!this.lessonTimeAssignment) return false;

    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[this.lessonTimeAssignment.dayOfWeek];
    if (now.getDay() !== lessonDay) return false;

    const [startH, startM] = this.lessonTimeAssignment.startTime.split(':').map(Number);
    const [endH, endM] = this.lessonTimeAssignment.endTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    // Enable button 15 minutes before start time until end time
    return nowMinutes >= startMinutes - 15 && nowMinutes <= endMinutes;
  }

  isInScheduledTime(): boolean {
    if (!this.lessonTimeAssignment) return false;
    
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[this.lessonTimeAssignment.dayOfWeek];
    if (now.getDay() !== lessonDay) return false;

    const [startH, startM] = this.lessonTimeAssignment.startTime.split(':').map(Number);
    const [endH, endM] = this.lessonTimeAssignment.endTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    // Check if current time is within the scheduled window (15 min before → end time)
    return nowMinutes >= startMinutes - 15 && nowMinutes <= endMinutes;
  }

  getTimeUntilStart(): string {
    if (!this.lessonTimeAssignment) return '';
    
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[this.lessonTimeAssignment.dayOfWeek];
    const currentDay = now.getDay();
    
    // Calculate days until lesson day
    let daysUntil = lessonDay - currentDay;
    if (daysUntil < 0) daysUntil += 7; // Next week
    
    const [startH, startM] = this.lessonTimeAssignment.startTime.split(':').map(Number);
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
      return `Available tomorrow at ${this.lessonTimeAssignment.startTime}`;
    } else {
      const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      return `Available on ${dayNames[lessonDay]} at ${this.lessonTimeAssignment.startTime}`;
    }
  }

  joinLesson(): void {
    if (!this.lesson?.id || !this.activeMeetingRoomId) return;

    // Use the actual room ID from the active meeting
    this.router.navigate(['/join', this.activeMeetingRoomId], {
      queryParams: { lessonId: this.lesson.id }
    });
  }

  // Quiz methods
  loadQuiz(quizId: number): void {
    this.loadingQuiz = true;
    
    // Check if student already attempted this quiz
    if (this.currentStudentId) {
      this.quizService.getStudentAttempts(this.currentStudentId).subscribe({
        next: (attempts) => {
          const existingAttempt = attempts.find(a => a.quizId === quizId && a.status === 'COMPLETED');
          if (existingAttempt?.id) {
            this.quizAttemptExists = true;
            // Load the result
            this.quizService.getAttemptResult(existingAttempt.id).subscribe({
              next: (result) => {
                this.quizResult = result;
                this.quizSubmitted = true;
                this.loadingQuiz = false;
              },
              error: () => {
                this.loadingQuiz = false;
              }
            });
            return;
          }
          
          // No attempt exists, load quiz normally
          this.loadQuizData(quizId);
        },
        error: () => {
          // If error checking attempts, still load quiz
          this.loadQuizData(quizId);
        }
      });
    } else {
      this.loadQuizData(quizId);
    }
  }

  loadQuizData(quizId: number): void {
    this.quizService.getQuizById(quizId).subscribe({
      next: (quiz) => {
        this.currentQuiz = quiz;
        // Load questions
        this.quizService.getQuestionsByQuizId(quizId).subscribe({
          next: (questions) => {
            this.quizQuestions = questions.sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
            this.loadingQuiz = false;
          },
          error: (error) => {
            console.error('Error loading questions:', error);
            this.loadingQuiz = false;
          }
        });
      },
      error: (error) => {
        console.error('Error loading quiz:', error);
        this.loadingQuiz = false;
      }
    });
  }

  startQuiz(): void {
    this.quizStarted = true;
    this.currentQuestionIndex = 0;
    this.studentAnswers = {};
    
    // Calculate time per question
    if (this.currentQuiz.durationMin && this.quizQuestions.length > 0) {
      this.totalTimeRemaining = this.currentQuiz.durationMin * 60; // Convert to seconds
      this.timePerQuestion = Math.floor(this.totalTimeRemaining / this.quizQuestions.length);
      this.questionTimeRemaining = this.timePerQuestion;
      this.startTimer();
    }
  }

  startTimer(): void {
    this.quizTimerInterval = setInterval(() => {
      this.totalTimeRemaining--;
      this.questionTimeRemaining--;
      
      // Check if total time is up
      if (this.totalTimeRemaining <= 0) {
        this.stopTimer();
        alert('Time is up! Your quiz will be submitted automatically.');
        this.autoSubmitQuiz();
      }
    }, 1000);
  }

  stopTimer(): void {
    if (this.quizTimerInterval) {
      clearInterval(this.quizTimerInterval);
      this.quizTimerInterval = null;
    }
  }

  resetQuestionTimer(): void {
    this.questionTimeRemaining = this.timePerQuestion;
  }

  getTotalFormattedTime(): string {
    const minutes = Math.floor(this.totalTimeRemaining / 60);
    const seconds = this.totalTimeRemaining % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  getQuestionFormattedTime(): string {
    return `${this.questionTimeRemaining}s`;
  }

  getTotalTimePercentage(): number {
    if (!this.currentQuiz.durationMin) return 100;
    const totalSeconds = this.currentQuiz.durationMin * 60;
    return (this.totalTimeRemaining / totalSeconds) * 100;
  }

  getQuestionTimePercentage(): number {
    if (this.timePerQuestion === 0) return 100;
    return (this.questionTimeRemaining / this.timePerQuestion) * 100;
  }

  selectAnswer(questionId: number, answer: string): void {
    this.studentAnswers[questionId] = answer;
  }

  isAnswerSelected(questionId: number, answer: string): boolean {
    return this.studentAnswers[questionId] === answer;
  }

  getCurrentQuestion(): any {
    const question = this.quizQuestions[this.currentQuestionIndex];
    // Debug log
    if (question?.type === 'TRUE_FALSE') {
      console.log('TRUE_FALSE Question:', question);
      console.log('Options:', question.options);
      console.log('Split options:', question.options?.split('|'));
    }
    return question;
  }

  nextQuestion(): void {
    if (this.currentQuestionIndex < this.quizQuestions.length - 1) {
      this.currentQuestionIndex++;
      this.resetQuestionTimer(); // Reset question timer when moving to next question
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
      this.resetQuestionTimer(); // Reset question timer when going back
    }
  }

  goToQuestion(index: number): void {
    this.currentQuestionIndex = index;
    this.resetQuestionTimer(); // Reset question timer when jumping to a question
  }

  isQuestionAnswered(index: number): boolean {
    const question = this.quizQuestions[index];
    return !!this.studentAnswers[question.id];
  }

  getAnsweredCount(): number {
    return Object.keys(this.studentAnswers).length;
  }

  canSubmitQuiz(): boolean {
    return this.getAnsweredCount() === this.quizQuestions.length;
  }

  submitQuiz(): void {
    if (!this.canSubmitQuiz() || !this.currentQuiz || !this.currentStudentId) return;

    const confirmed = confirm(`Are you sure you want to submit? You have answered ${this.getAnsweredCount()} out of ${this.quizQuestions.length} questions.`);
    if (!confirmed) return;

    // Stop timer
    this.stopTimer();

    this.performQuizSubmission();
  }

  autoSubmitQuiz(): void {
    if (!this.currentQuiz || !this.currentStudentId) return;
    this.stopTimer();
    this.performQuizSubmission();
  }

  performQuizSubmission(): void {
    console.log('Submitting quiz with answers:', this.studentAnswers);

    // Start attempt and submit
    this.quizService.startAttempt(this.currentQuiz.id, this.currentStudentId).subscribe({
      next: (attempt) => {
        console.log('Attempt started:', attempt);
        
        if (!attempt.id) {
          alert('Failed to start quiz attempt. Please try again.');
          return;
        }
        
        const attemptRequest: any = {
          quizId: this.currentQuiz.id,
          studentId: this.currentStudentId,
          answers: this.studentAnswers
        };

        console.log('Submitting attempt request:', attemptRequest);

        this.quizService.submitAttempt(attempt.id, attemptRequest).subscribe({
          next: (result) => {
            console.log('Quiz result:', result);
            this.quizResult = result;
            this.quizSubmitted = true;
            
            // 🎯 TRACKER L'ÉVALUATION DANS LES ANALYTICS
            const score = Math.round((result.score / result.maxScore) * 100);
            this.trackQuizCompletion(this.currentStudentId, score, result.passed);
            
            // Mark lesson as complete if passed
            if (result.passed) {
              this.markAsComplete();
            }
          },
          error: (error) => {
            console.error('Error submitting quiz:', error);
            alert('Failed to submit quiz. Please try again.');
          }
        });
      },
      error: (error) => {
        console.error('Error starting attempt:', error);
        alert('Failed to start quiz attempt. Please try again.');
      }
    });
  }

  retakeQuiz(): void {
    // Cannot retake quiz - show message
    alert('You have already completed this quiz. You cannot retake it.');
  }

  /**
   * 🎯 Track l'évaluation dans les analytics
   */
  private trackQuizCompletion(userId: number, score: number, passed: boolean): void {
    // Déterminer le type d'évaluation (TMA, CMA ou EXAM)
    const assessmentType = this.determineAssessmentType();
    
    // Tracker l'évaluation
    this.analyticsService.trackAssessment(userId, score, assessmentType).subscribe({
      next: () => console.log(`✅ Quiz tracké: ${assessmentType} - Score: ${score}%`),
      error: (err: any) => console.error('❌ Erreur tracking quiz:', err)
    });

    // Si l'étudiant a échoué, incrémenter les tentatives
    if (!passed) {
      this.analyticsService.incrementAttempts(userId).subscribe({
        next: () => console.log('✅ Tentative incrémentée'),
        error: (err: any) => console.error('❌ Erreur incrémentation tentative:', err)
      });
    }
  }

  /**
   * Détermine le type d'évaluation basé sur le nom du quiz
   */
  private determineAssessmentType(): 'TMA' | 'CMA' | 'EXAM' {
    const quizName = this.currentQuiz?.name?.toLowerCase() || '';
    
    if (quizName.includes('exam') || quizName.includes('final')) {
      return 'EXAM';
    } else if (quizName.includes('tma') || quizName.includes('tutor')) {
      return 'TMA';
    } else {
      return 'CMA'; // Par défaut, Computer Marked Assignment
    }
  }

}
