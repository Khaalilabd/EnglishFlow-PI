import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { Course } from '../../../core/models/course.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Lesson, LessonType, LessonTypeIcons } from '../../../core/models/lesson.model';
import { CourseService } from '../../../core/services/course.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonService } from '../../../core/services/lesson.service';

@Component({
  selector: 'app-courses-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './courses-details.component.html',
  styleUrls: ['./courses-details.component.scss']
})
export class CoursesDetailsComponent implements OnInit {
  course: Course | null = null;
  chapters: Chapter[] = [];
  lessonsByChapter: Map<number, Lesson[]> = new Map();
  showDeleteModal = false;
  activeTab: 'overview' | 'curriculum' | 'materials' | 'students' = 'overview';
  expandedSections: Set<number> = new Set();
  loading = true;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private chapterService: ChapterService,
    private lessonService: LessonService
  ) {}

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
        this.chapters = chapters;
        // Load lessons for each chapter
        chapters.forEach(chapter => {
          if (chapter.id) {
            this.loadLessons(chapter.id);
          }
        });
      },
      error: (error) => {
        console.error('Error loading chapters:', error);
      }
    });
  }

  loadLessons(chapterId: number): void {
    this.lessonService.getLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.lessonsByChapter.set(chapterId, lessons);
      },
      error: (error) => {
        console.error('Error loading lessons:', error);
      }
    });
  }

  getChapterLessons(chapterId: number): Lesson[] {
    return this.lessonsByChapter.get(chapterId) || [];
  }

  toggleSection(sectionId: number): void {
    if (this.expandedSections.has(sectionId)) {
      this.expandedSections.delete(sectionId);
    } else {
      this.expandedSections.add(sectionId);
    }
  }

  isSectionExpanded(sectionId: number): boolean {
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

  getLessonIcon(type: LessonType): string {
    return LessonTypeIcons[type] || 'fa-file';
  }

  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
      return `${hours}h ${mins}m`;
    }
    return `${mins}m`;
  }

  goBack(): void {
    this.router.navigate(['/dashboard/courses']);
  }

  editCourse(): void {
    if (this.course) {
      this.router.navigate(['/dashboard/courses', this.course.id, 'edit']);
    }
  }

  deleteCourse(): void {
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.course && this.course.id) {
      this.courseService.deleteCourse(this.course.id).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/courses']);
        },
        error: (error) => {
          console.error('Error deleting course:', error);
          this.closeDeleteModal();
        }
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
  }
}

