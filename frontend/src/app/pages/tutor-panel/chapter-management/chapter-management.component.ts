import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ChapterService } from '../../../core/services/chapter.service';
import { CourseService } from '../../../core/services/course.service';
import { Chapter, CreateChapterRequest, UpdateChapterRequest } from '../../../core/models/chapter.model';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-chapter-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chapter-management.component.html',
  styleUrl: './chapter-management.component.scss'
})
export class ChapterManagementComponent implements OnInit {
  courseId!: number;
  course: Course | null = null;
  chapters: Chapter[] = [];
  loading = false;
  
  // Modal states
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  
  // Form data
  chapterForm: CreateChapterRequest = {
    title: '',
    description: '',
    objectives: [],
    orderIndex: 0,
    estimatedDuration: 0,
    isPublished: false,
    courseId: 0
  };
  
  newObjective = '';
  selectedChapter: Chapter | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private chapterService: ChapterService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.chapterForm.courseId = this.courseId;
    this.loadCourse();
    this.loadChapters();
  }

  loadCourse(): void {
    this.courseService.getCourseById(this.courseId).subscribe({
      next: (course) => {
        this.course = course;
      },
      error: (error) => {
        console.error('Error loading course:', error);
      }
    });
  }

  loadChapters(): void {
    this.loading = true;
    this.chapterService.getChaptersByCourse(this.courseId).subscribe({
      next: (chapters) => {
        this.chapters = chapters.sort((a, b) => a.orderIndex - b.orderIndex);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading chapters:', error);
        this.loading = false;
      }
    });
  }

  openCreateModal(): void {
    this.chapterForm = {
      title: '',
      description: '',
      objectives: [],
      orderIndex: this.chapters.length,
      estimatedDuration: 0,
      isPublished: false,
      courseId: this.courseId
    };
    this.newObjective = '';
    this.showCreateModal = true;
  }

  openEditModal(chapter: Chapter): void {
    this.selectedChapter = chapter;
    this.chapterForm = {
      title: chapter.title,
      description: chapter.description,
      objectives: chapter.objectives ? [...chapter.objectives] : [],
      orderIndex: chapter.orderIndex,
      estimatedDuration: chapter.estimatedDuration || 0,
      isPublished: chapter.isPublished,
      courseId: chapter.courseId
    };
    this.newObjective = '';
    this.showEditModal = true;
  }

  openDeleteModal(chapter: Chapter): void {
    this.selectedChapter = chapter;
    this.showDeleteModal = true;
  }

  closeModals(): void {
    this.showCreateModal = false;
    this.showEditModal = false;
    this.showDeleteModal = false;
    this.selectedChapter = null;
  }

  addObjective(): void {
    if (this.newObjective.trim()) {
      if (!this.chapterForm.objectives) {
        this.chapterForm.objectives = [];
      }
      this.chapterForm.objectives.push(this.newObjective.trim());
      this.newObjective = '';
    }
  }

  removeObjective(index: number): void {
    if (this.chapterForm.objectives) {
      this.chapterForm.objectives.splice(index, 1);
    }
  }

  createChapter(): void {
    this.loading = true;
    this.chapterService.createChapter(this.chapterForm).subscribe({
      next: () => {
        this.loadChapters();
        this.closeModals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error creating chapter:', error);
        this.loading = false;
      }
    });
  }

  updateChapter(): void {
    if (!this.selectedChapter?.id) return;
    
    this.loading = true;
    const updateRequest: UpdateChapterRequest = {
      title: this.chapterForm.title,
      description: this.chapterForm.description,
      objectives: this.chapterForm.objectives,
      orderIndex: this.chapterForm.orderIndex,
      estimatedDuration: this.chapterForm.estimatedDuration,
      isPublished: this.chapterForm.isPublished,
      courseId: this.courseId
    };
    
    this.chapterService.updateChapter(this.selectedChapter.id, updateRequest).subscribe({
      next: () => {
        this.loadChapters();
        this.closeModals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error updating chapter:', error);
        this.loading = false;
      }
    });
  }

  deleteChapter(): void {
    if (!this.selectedChapter?.id) return;
    
    this.loading = true;
    this.chapterService.deleteChapter(this.selectedChapter.id).subscribe({
      next: () => {
        this.loadChapters();
        this.closeModals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error deleting chapter:', error);
        this.loading = false;
      }
    });
  }

  togglePublish(chapter: Chapter): void {
    if (!chapter.id) return;
    
    const updateRequest: UpdateChapterRequest = {
      title: chapter.title,
      description: chapter.description,
      objectives: chapter.objectives,
      orderIndex: chapter.orderIndex,
      estimatedDuration: chapter.estimatedDuration,
      isPublished: !chapter.isPublished,
      courseId: chapter.courseId
    };
    
    this.chapterService.updateChapter(chapter.id, updateRequest).subscribe({
      next: () => {
        this.loadChapters();
      },
      error: (error) => {
        console.error('Error toggling publish status:', error);
      }
    });
  }

  manageLessons(chapter: Chapter): void {
    this.router.navigate(['/tutor-panel/courses', this.courseId, 'chapters', chapter.id, 'lessons']);
  }

  goBack(): void {
    this.router.navigate(['/tutor-panel/courses', this.courseId]);
  }

  getTotalDuration(): number {
    return this.chapters.reduce((sum, ch) => sum + (ch.estimatedDuration || 0), 0);
  }

  getPublishedCount(): number {
    return this.chapters.filter(ch => ch.isPublished).length;
  }
}
