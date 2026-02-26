import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../../core/services/course.service';
import { ChapterService } from '../../../core/services/chapter.service';
import { LessonService } from '../../../core/services/lesson.service';
import { Course, CourseStatus } from '../../../core/models/course.model';
import { Chapter } from '../../../core/models/chapter.model';
import { Lesson } from '../../../core/models/lesson.model';

@Component({
  selector: 'app-course-status-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-status-management.component.html',
  styleUrls: ['./course-status-management.component.scss']
})
export class CourseStatusManagementComponent implements OnInit {
  courses: Course[] = [];
  selectedCourse: Course | null = null;
  chapters: Chapter[] = [];
  lessons: { [chapterId: number]: Lesson[] } = {};
  expandedChapters: Set<number> = new Set();
  loading = false;
  error: string | null = null;
  searchTerm = '';
  CourseStatus = CourseStatus;

  constructor(
    private courseService: CourseService,
    private chapterService: ChapterService,
    private lessonService: LessonService
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.loading = true;
    this.error = null;
    
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load courses';
        this.loading = false;
        console.error('Error loading courses:', err);
      }
    });
  }

  selectCourse(course: Course): void {
    this.selectedCourse = course;
    this.loadChapters(course.id!);
  }

  loadChapters(courseId: number): void {
    this.chapterService.getChaptersByCourse(courseId).subscribe({
      next: (chapters) => {
        this.chapters = chapters.sort((a, b) => a.orderIndex - b.orderIndex);
      },
      error: (err) => console.error('Error loading chapters:', err)
    });
  }

  toggleChapter(chapterId: number): void {
    if (this.expandedChapters.has(chapterId)) {
      this.expandedChapters.delete(chapterId);
    } else {
      this.expandedChapters.add(chapterId);
      if (!this.lessons[chapterId]) {
        this.loadLessons(chapterId);
      }
    }
  }

  loadLessons(chapterId: number): void {
    this.lessonService.getLessonsByChapter(chapterId).subscribe({
      next: (lessons) => {
        this.lessons[chapterId] = lessons.sort((a, b) => a.orderIndex - b.orderIndex);
      },
      error: (err) => console.error('Error loading lessons:', err)
    });
  }

  updateCourseStatus(course: Course, isPublished: boolean): void {
    const updatedCourse = { ...course, status: isPublished ? CourseStatus.PUBLISHED : CourseStatus.DRAFT };
    
    this.courseService.updateCourse(course.id!, updatedCourse).subscribe({
      next: () => {
        course.status = isPublished ? CourseStatus.PUBLISHED : CourseStatus.DRAFT;
      },
      error: (err) => console.error('Error updating course status:', err)
    });
  }

  updateChapterStatus(chapter: Chapter, isPublished: boolean): void {
    const updatedChapter = { ...chapter, isPublished };
    
    this.chapterService.updateChapter(chapter.id!, updatedChapter).subscribe({
      next: () => {
        chapter.isPublished = isPublished;
      },
      error: (err) => console.error('Error updating chapter status:', err)
    });
  }

  updateLessonStatus(lesson: Lesson, isPublished: boolean): void {
    const updatedLesson = { ...lesson, isPublished };
    
    this.lessonService.updateLesson(lesson.id!, updatedLesson).subscribe({
      next: () => {
        lesson.isPublished = isPublished;
      },
      error: (err) => console.error('Error updating lesson status:', err)
    });
  }

  get filteredCourses(): Course[] {
    if (!this.searchTerm) return this.courses;
    
    const term = this.searchTerm.toLowerCase();
    return this.courses.filter(course =>
      course.title.toLowerCase().includes(term) ||
      course.description?.toLowerCase().includes(term) ||
      course.tutorName?.toLowerCase().includes(term) ||
      course.category?.toLowerCase().includes(term)
    );
  }

  getPublishedCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.PUBLISHED).length;
  }

  getDraftCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.DRAFT).length;
  }

  getArchivedCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.ARCHIVED).length;
  }

  backToCourseList(): void {
    this.selectedCourse = null;
    this.chapters = [];
    this.lessons = {};
    this.expandedChapters.clear();
  }
}
