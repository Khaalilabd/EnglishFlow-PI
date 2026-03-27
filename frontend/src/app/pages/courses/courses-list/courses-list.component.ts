import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course, CourseStatus, CEFR_LEVELS } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-courses-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses-list.component.html',
  styleUrls: ['./courses-list.component.scss']
})
export class CoursesListComponent implements OnInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  loading = false;
  searchTerm = '';
  selectedLevel = 'ALL';
  selectedStatus = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Delete Modal
  showDeleteModal = false;
  courseToDelete: Course | null = null;

  // Expose enums to template
  levels = CEFR_LEVELS;
  CourseStatus = CourseStatus;

  constructor(
    private router: Router,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.loading = true;
    console.log('Loading courses from:', `${environment.apiUrl}/courses`);
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        console.log('Courses loaded successfully:', courses);
        this.courses = courses;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
        this.loading = false;
        this.courses = [];
        this.applyFilters();
        alert('Failed to load courses. Please check the console for details.');
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.courses];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(course =>
        course.title.toLowerCase().includes(term) ||
        course.description.toLowerCase().includes(term)
      );
    }

    // Level filter
    if (this.selectedLevel !== 'ALL') {
      filtered = filtered.filter(course => course.level === this.selectedLevel);
    }

    // Status filter
    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(course => course.status === this.selectedStatus);
    }

    this.filteredCourses = filtered;
    this.totalPages = Math.ceil(this.filteredCourses.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  get paginatedCourses(): Course[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredCourses.slice(start, end);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // Action methods
  createCourse(): void {
    // Detect current route context
    const currentUrl = this.router.url;
    if (currentUrl.includes('/tutor-panel')) {
      this.router.navigate(['/tutor-panel/courses/create']);
    } else {
      this.router.navigate(['/dashboard/courses/create']);
    }
  }

  viewCourse(course: Course): void {
    // Detect current route context
    const currentUrl = this.router.url;
    if (currentUrl.includes('/tutor-panel')) {
      this.router.navigate(['/tutor-panel/courses', course.id]);
    } else {
      this.router.navigate(['/dashboard/courses', course.id]);
    }
  }

  editCourse(course: Course): void {
    // Detect current route context
    const currentUrl = this.router.url;
    if (currentUrl.includes('/tutor-panel')) {
      this.router.navigate(['/tutor-panel/courses', course.id, 'edit']);
    } else {
      this.router.navigate(['/dashboard/courses', course.id, 'edit']);
    }
  }

  toggleCourseStatus(course: Course): void {
    if (!course.id) return;
    
    const newStatus = course.status === CourseStatus.PUBLISHED ? CourseStatus.DRAFT : CourseStatus.PUBLISHED;
    
    this.courseService.updateCourse(course.id, {
      ...course,
      status: newStatus
    }).subscribe({
      next: (updatedCourse) => {
        course.status = updatedCourse.status;
      },
      error: (error) => {
        console.error('Error updating course status:', error);
        alert('Failed to update course status');
      }
    });
  }

  deleteCourse(course: Course): void {
    this.courseToDelete = course;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.courseToDelete && this.courseToDelete.id) {
      this.courseService.deleteCourse(this.courseToDelete.id).subscribe({
        next: () => {
          this.courses = this.courses.filter(c => c.id !== this.courseToDelete!.id);
          this.applyFilters();
          this.closeDeleteModal();
        },
        error: (error) => {
          console.error('Error deleting course:', error);
          alert('Failed to delete course');
          this.closeDeleteModal();
        }
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.courseToDelete = null;
  }

  // Stats methods
  getPublishedCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.PUBLISHED).length;
  }

  getDraftCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.DRAFT).length;
  }

  getArchivedCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.ARCHIVED).length;
  }

  getTotalStudents(): number {
    return this.courses.reduce((sum, c) => sum + (c.maxStudents || 0), 0);
  }

  // Helper methods
  getLevelIcon(level: string): string {
    const icons: Record<string, string> = {
      'A1': '📚',
      'A2': '📖',
      'B1': '💬',
      'B2': '💼',
      'C1': '🎓',
      'C2': '🏆'
    };
    return icons[level] || '📚';
  }

  getLevelColor(level: string): string {
    const colors: Record<string, string> = {
      'A1': 'bg-green-100',
      'A2': 'bg-blue-100',
      'B1': 'bg-yellow-100',
      'B2': 'bg-orange-100',
      'C1': 'bg-purple-100',
      'C2': 'bg-red-100'
    };
    return colors[level] || 'bg-gray-100';
  }

  getStatusColor(status: CourseStatus): string {
    const colors = {
      [CourseStatus.PUBLISHED]: 'bg-green-100 text-green-800',
      [CourseStatus.DRAFT]: 'bg-yellow-100 text-yellow-800',
      [CourseStatus.ARCHIVED]: 'bg-gray-100 text-gray-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '0h';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0 && mins > 0) {
      return `${hours}h ${mins}m`;
    } else if (hours > 0) {
      return `${hours}h`;
    }
    return `${mins}m`;
  }

  get Math() {
    return Math;
  }
}
