import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { Course } from '../../../core/models/course.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Lesson, LessonType } from '../../../core/models/lesson.model';
import { CourseReview } from '../../../core/models/review.model';
import { CourseService } from '../../../core/services/course.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonService } from '../../../core/services/lesson.service';

@Component({
  selector: 'app-course-view',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './course-view.component.html',
  styleUrls: ['./course-view.component.scss']
})
export class CourseViewComponent implements OnInit {
  course: Course | null = null;
  chapters: Chapter[] = [];
  lessonsByChapter: Map<number, Lesson[]> = new Map();
  reviews: CourseReview[] = [];
  isEnrolled = false;
  loading = true;
  expandedSections: Set<number> = new Set();
  activeTab: 'overview' | 'curriculum' | 'reviews' = 'overview';
  
  // Review form
  userRating = 0;
  userComment = '';
  hoverRating = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private chapterService: ChapterService,
    private lessonService: LessonService
  ) {}

  ngOnInit(): void {
    this.loadCourse();
    this.loadReviews();
    this.checkEnrollment();
  }

  loadCourse(): void {
    const courseId = Number(this.route.snapshot.paramMap.get('id'));
    if (courseId) {
      this.courseService.getCourseById(courseId).subscribe({
        next: (course) => {
          this.course = course;
          this.loadChapters(courseId);
        },
        error: (error) => {
          console.error('Error loading course:', error);
          this.loading = false;
        }
      });
    }
  }

  loadChapters(courseId: number): void {
    this.chapterService.getPublishedChaptersByCourse(courseId).subscribe({
      next: (chapters) => {
        this.chapters = chapters;
        // Load lessons for each chapter
        chapters.forEach(chapter => {
          if (chapter.id) {
            this.loadLessons(chapter.id);
          }
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading chapters:', error);
        this.loading = false;
      }
    });
  }

  loadLessons(chapterId: number): void {
    this.lessonService.getPublishedLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.lessonsByChapter.set(chapterId, lessons);
      },
      error: (error) => {
        console.error('Error loading lessons:', error);
      }
    });
  }

  loadReviews(): void {
    // TODO: Implement reviews API when available
    this.reviews = [];
  }

  checkEnrollment(): void {
    // TODO: Check if user is enrolled via API
    this.isEnrolled = false;
  }

  enrollCourse(): void {
    if (this.course) {
      // TODO: API call to enroll
      this.isEnrolled = true;
      alert('Successfully enrolled in the course!');
    }
  }

  toggleSection(sectionId: number | undefined): void {
    if (!sectionId) return;
    if (this.expandedSections.has(sectionId)) {
      this.expandedSections.delete(sectionId);
    } else {
      this.expandedSections.add(sectionId);
    }
  }

  isSectionExpanded(sectionId: number | undefined): boolean {
    if (!sectionId) return false;
    return this.expandedSections.has(sectionId);
  }

  getTotalLessons(): number {
    let total = 0;
    this.lessonsByChapter.forEach(lessons => {
      total += lessons.length;
    });
    return total;
  }

  getTotalDuration(): number {
    let total = 0;
    this.lessonsByChapter.forEach(lessons => {
      lessons.forEach(lesson => {
        total += lesson.duration || 0;
      });
    });
    return total;
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '0m';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
      return `${hours}h ${mins}m`;
    }
    return `${mins}m`;
  }

  getLessonIcon(type: LessonType): string {
    const icons: { [key in LessonType]: string } = {
      [LessonType.VIDEO]: '🎥',
      [LessonType.TEXT]: '📄',
      [LessonType.FILE]: '📁',
      [LessonType.IMAGE]: '🖼️',
      [LessonType.MIXED]: '📚'
    };
    return icons[type] || '📚';
  }

  getChapterLessons(chapterId: number | undefined): Lesson[] {
    if (!chapterId) return [];
    return this.lessonsByChapter.get(chapterId) || [];
  }

  setRating(rating: number): void {
    this.userRating = rating;
  }

  setHoverRating(rating: number): void {
    this.hoverRating = rating;
  }

  submitReview(): void {
    if (this.userRating === 0) {
      alert('Please select a rating');
      return;
    }
    if (!this.userComment.trim()) {
      alert('Please write a comment');
      return;
    }

    // TODO: API call to submit review
    const newReview: CourseReview = {
      id: this.reviews.length + 1,
      courseId: this.course?.id || 0,
      userId: 1,
      userName: 'You',
      userAvatar: 'https://i.pravatar.cc/100?img=99',
      rating: this.userRating,
      comment: this.userComment,
      createdAt: new Date().toISOString(),
      helpful: 0
    };

    this.reviews.unshift(newReview);
    this.userRating = 0;
    this.userComment = '';
    alert('Review submitted successfully!');
  }

  markHelpful(review: CourseReview): void {
    review.helpful++;
    // TODO: API call to mark helpful
  }

  goBack(): void {
    this.router.navigate(['/user-panel/course-catalog']);
  }
}
