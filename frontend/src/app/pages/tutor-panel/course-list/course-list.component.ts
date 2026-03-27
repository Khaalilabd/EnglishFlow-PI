import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course, CourseStatus } from '../../../core/models/course.model';
import { CourseCategory } from '../../../core/models/course-category.model';
import { CourseService } from '../../../core/services/course.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.scss']
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  categories: CourseCategory[] = [];
  loading = false;
  
  // Filters
  searchTerm = '';
  selectedCategory = 'ALL';
  selectedLevel = 'ALL';
  selectedStatus = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 9;
  
  // Delete Modal
  showDeleteModal = false;
  courseToDelete: Course | null = null;
  
  // Constants
  levels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  statuses = Object.values(CourseStatus);

  constructor(
    private router: Router,
    private courseService: CourseService,
    private categoryService: CourseCategoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadCourses();
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadCourses(): void {
    this.loading = true;
    const currentUser = this.authService.currentUserValue;
    
    if (currentUser) {
      this.courseService.getCoursesByTutor(currentUser.id).subscribe({
        next: (courses) => {
          this.courses = courses;
          this.applyFilters();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading courses:', error);
          this.loading = false;
        }
      });
    }
  }

  applyFilters(): void {
    let filtered = [...this.courses];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(course =>
        course.title.toLowerCase().includes(term) ||
        course.description.toLowerCase().includes(term)
      );
    }

    if (this.selectedCategory !== 'ALL') {
      filtered = filtered.filter(course => course.category === this.selectedCategory);
    }

    if (this.selectedLevel !== 'ALL') {
      filtered = filtered.filter(course => course.level === this.selectedLevel);
    }

    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(course => course.status === this.selectedStatus);
    }

    this.filteredCourses = filtered;
    this.currentPage = 1;
  }

  get paginatedCourses(): Course[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredCourses.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredCourses.length / this.itemsPerPage);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
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

  createCourse(): void {
    this.router.navigate(['/tutor-panel/courses/create']);
  }

  viewCourse(course: Course): void {
    this.router.navigate(['/tutor-panel/courses', course.id]);
  }

  editCourse(course: Course): void {
    this.router.navigate(['/tutor-panel/courses/edit', course.id]);
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

  toggleStatus(course: Course): void {
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

  // Stats
  getTotalCourses(): number {
    return this.courses.length;
  }

  getPublishedCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.PUBLISHED).length;
  }

  getDraftCount(): number {
    return this.courses.filter(c => c.status === CourseStatus.DRAFT).length;
  }

  getTotalStudents(): number {
    return this.courses.reduce((sum, c) => sum + (c.maxStudents || 0), 0);
  }

  // Helpers
  getCategoryIcon(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.icon || '📚';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.color || '#3B82F6';
  }

  getStatusColor(status: CourseStatus): string {
    const colors = {
      [CourseStatus.PUBLISHED]: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300',
      [CourseStatus.DRAFT]: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-300',
      [CourseStatus.ARCHIVED]: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
    };
    return colors[status];
  }

  get Math() {
    return Math;
  }
}
